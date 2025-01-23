// src/controllers/postController.ts
import { Request, Response } from "express";
import { v4 as uuidv4 } from "uuid";
import { withSession } from "../services/session";  // อาจจะต้องสร้าง service สำหรับ session
import { errorHandler } from "../utils/errorHandler"; // error handler
import { Post } from "../types"; // คุณสามารถสร้างประเภท Post ในไฟล์ types.ts

export const createPost = async (req: Request, res: Response): Promise<void> => {
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
        })
        MERGE (u:User {id: $userId})
        CREATE (u)-[:CREATED_BY]->(p)
        RETURN p`,
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
  } catch (error) {
    errorHandler(error, res);
  }
};

export const getAllPosts = async (req: Request, res: Response): Promise<void> => {
  try {
    console.log('GET /api/posts request received');
    const posts = await withSession(async (session) => {
      const result = await session.run(
        `MATCH (u:User)-[]->(p:Post)
         RETURN p, u.username
         ORDER BY p.createdAt DESC`
      );

      return result.records.map((record) => {
        const post = record.get("p").properties;
        const username = record.get("u.username");
        return { ...post, username };
      });
    });

    if (posts.length === 0) {
      res.status(200).json({ posts: [] });
      return;
    }
    

    res.status(200).json({ posts });
  } catch (error) {
    errorHandler(error, res);
  }
};

export const updatePost = async (req: Request, res: Response): Promise<void> => {
  const { id } = req.params;
  const { content } = req.body;

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
};

export const deletePost = async (req: Request, res: Response): Promise<void> => {
  try {
    const { postId } = req.params;
    const { userId } = req.body;

    if (!userId) {
      res.status(400).json({ message: "User ID is required for authorization." });
      return;
    }

    const post = await withSession(async (session) => {
      const result = await session.run(
        `MATCH (p:Post {id: $postId}) RETURN p`,
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

    await withSession(async (session) => {
      await session.run(`MATCH (p:Post {id: $postId}) DELETE p`, { postId });
    });

    res.status(200).json({ message: "Post deleted successfully." });
  } catch (error) {
    errorHandler(error, res);
  }
};
