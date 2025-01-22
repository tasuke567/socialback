import express, { Request, Response, NextFunction } from "express";
import { driver } from "../database/neo4j";
import { v4 as uuidv4 } from "uuid";
import { error, Session } from "neo4j-driver";
import { console } from "inspector";

interface Post {
  id: string;
  userId: string;
  content: string;
  createdAt: string;
}

const router = express.Router();

// Middleware for error handling
const errorHandler = (error: any, res: Response): void => {
    console.error("Error:", error);
    const status = error.status || 500;
    const message = error.status ? error.message : "Internal Server Error";
    res.status(status).json({ message, error: error.message });
  };
  
// Middleware for session management
const withSession = async (
  operation: (session: Session) => Promise<any>
): Promise<any> => {
  const session = driver.session();
  try {
    return await operation(session);
  } finally {
    await session.close();
  }
};

// Validation middleware
const validatePost = (
  req: Request,
  res: Response,
  next: NextFunction
): void => {
  const { userId, content } = req.body;

  if (!userId || typeof userId !== "string") {
    res.status(400).json({ message: "Valid user ID is required." });
    return;
  }

  if (!content || typeof content !== "string" || content.length < 1) {
    res.status(400).json({ message: "Valid content is required." });
    return;
  }

  if (content.length > 1000) {
    res
      .status(400)
      .json({ message: "Content must be less than 1000 characters." });
    return;
  }

  next();
};

// Create post
router.post(
    "/posts",
    validatePost,
    async (req: Request, res: Response): Promise<void> => {
      try {
        const { userId, content } = req.body;
  
        const post = await withSession(async (session) => {
          const result = await session.run(
            `CREATE (p:Post {
              id: $postId,
              userId: $userId,
              content: $content,
              createdAt: datetime(),
              updatedAt: datetime()
            }) RETURN p`,
            {
              postId: uuidv4(),
              userId,
              content,
            }
          );
  
          return result.records[0].get("p").properties;
        });
  
        res.status(201).json({
          message: "Post created successfully",
          post,
        });
      } catch (error: any) {
        errorHandler(error, res);
      }
    }
  );
  

// Get all posts without pagination
router.get("/posts", async (req: Request, res: Response): Promise<void> => {
  try {
    const posts = await withSession(async (session) => {
      const result = await session.run(
        `MATCH (p:Post)
             RETURN p
             ORDER BY p.createdAt DESC`
      );

      return result.records.map((record) => record.get("p").properties);
    });

    if (posts.length === 0) {
      res.status(404).json({ message: "No posts found." });
      return;
    }

    res.status(200).json({ posts });
  } catch (error: any) {
    console.error("Error caught:", error);
    errorHandler(error, res);
  }
});

// Get user posts without pagination
router.get(
  "/posts/:userId",
  async (req: Request, res: Response): Promise<void> => {
    try {
      const { userId } = req.params;

      if (!userId) {
        res.status(400).json({ message: "User ID is required." });
        return;
      }

      const posts = await withSession(async (session) => {
        const result = await session.run(
          `MATCH (p:Post {userId: $userId})
           RETURN p
           ORDER BY p.createdAt DESC`,
          { userId }
        );

        return result.records.map((record) => record.get("p").properties);
      });

      if (posts.length === 0) {
        res.status(404).json({ message: "No posts found for this user." });
        return;
      }

      res.status(200).json({ posts });
    } catch (error: any) {
      errorHandler(error, res);
    }
  }
);

router.put("/posts/:id", async (req: Request, res: Response) => {
    const { id } = req.params;
    const { content } = req.body;
  
    console.log("Updating post with id:", id);
    console.log("New content:", content);
  
    try {
      const post = await withSession(async (session) => {
        const result = await session.run(
          `MATCH (p:Post {id: $id})
           SET p.content = $content, p.updatedAt = datetime()
           RETURN p`,
          { id, content }
        );
        return result.records[0]?.get("p")?.properties;
      });
  
      if (!post) {
        res.status(404).json({ message: "Post not found." });
        return;
      }
  
      res.status(200).json({ message: "Post updated successfully", post });
    } catch (error) {
      errorHandler(error, res);
    }
  });
  

// Delete post with authorization check
router.delete(
  "/posts/:postId",
  async (req: Request, res: Response): Promise<void> => {
    try {
      const { postId } = req.params;
      const { userId } = req.body;

      if (!userId) {
        res
          .status(400)
          .json({ message: "User ID is required for authorization." });
        return;
      }

      // Check if post exists and belongs to user
      const post = await withSession(async (session) => {
        const result = await session.run(
          `MATCH (p:Post {id: $postId})
         RETURN p`,
          { postId }
        );

        return result.records[0]?.get("p")?.properties;
      });

      if (!post) {
        res.status(404).json({ message: "Post not found." });
        return;
      }

      if (post.userId !== userId) {
        res.status(403).json({ message: "Unauthorized to delete this post." });
        return;
      }

      // Delete the post
      await withSession(async (session) => {
        await session.run(
          `MATCH (p:Post {id: $postId})
         DELETE p`,
          { postId }
        );
      });

      res.status(200).json({ message: "Post deleted successfully." });
    } catch (error: any) {
      errorHandler(error, res);
    }
  }
);

export default router;
