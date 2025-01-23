import { NextFunction, Request, Response } from "express";
import { driver, checkDatabaseConnection } from "../services/session";
import { Record } from "neo4j-driver";
import { searchFriendsByUsername } from "../services/friendService";


// Define the User interface
interface User {
  id: string;
  name: string;
}

// Add a friend
export const addFriend = async (req: Request, res: Response): Promise<void> => {
  const { userId, friendId } = req.params;
  const session = driver.session();

  try {
    await checkDatabaseConnection();

    // Check if they are already friends
    const checkResult = await session.run(
      `
      MATCH (a:User {id: $userId})-[:FRIEND]->(b:User {id: $friendId})
      RETURN a, b
      `,
      { userId, friendId }
    );

    if (checkResult.records.length > 0) {
      res.status(400).json({ message: "Users are already friends." });
      return;
    }

    // Create a friendship relationship
    await session.run(
      `
      MATCH (a:User {id: $userId}), (b:User {id: $friendId})
      MERGE (a)-[:FRIEND]->(b)
      `,
      { userId, friendId }
    );

    res.status(201).json({ message: "Friendship created successfully." });
  } catch (error) {
    console.error("Error adding friend:", error);
    res.status(500).json({ error: "An error occurred while adding a friend." });
  } finally {
    await session.close();
  }
};

// Get friends list
export const getFriends = async (req: Request, res: Response): Promise<void> => {
  const { userId } = req.params; // Extracts userId from the URL
  const session = driver.session();

  try {
    await checkDatabaseConnection();

    const result = await session.run(
      `
        MATCH (:User {id: $userId})-[:FRIEND]->(friend)
        RETURN friend
      `,
      { userId }
    );

    const friends: User[] = result.records.map((record: Record) => {
      const friendNode = record.get("friend");
      return friendNode.properties as User;
    });

    // Sort friends by ID
    friends.sort((a, b) => a.id.localeCompare(b.id));

    res.status(200).json({ userId, friends }); // Return friends list instead of name
  } catch (error) {
    console.error("Error fetching friends:", error);
    res.status(500).json({ error: "An error occurred while fetching friends." });
  } finally {
    await session.close();
  }
};

// Remove a friendship
export const unfriend = async (req: Request, res: Response): Promise<void> => {
  const { userId, friendId } = req.params;
  const session = driver.session();

  try {
    await checkDatabaseConnection();

    // Check if friendship exists
    const checkResult = await session.run(
      `
      MATCH (a:User {id: $userId})-[r:FRIEND]->(b:User {id: $friendId})
      RETURN r
      `,
      { userId, friendId }
    );

    if (checkResult.records.length === 0) {
      res.status(404).json({ message: "Friendship not found." });
      return;
    }

    // Remove friendship
    await session.run(
      `
      MATCH (a:User {id: $userId})-[r:FRIEND]->(b:User {id: $friendId})
      DELETE r
      `,
      { userId, friendId }
    );

    res.status(200).json({ message: "Friendship removed successfully." });
  } catch (error) {
    console.error("Error removing friend:", error);
    res.status(500).json({ error: "An error occurred while removing the friend." });
  } finally {
    await session.close();
  }
};

// Search friends by query
export const searchFriends = (
  async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const query = req.query.query as string; // Ensure it's a string

      if (!query) {
        res.status(400).json({
          message: "ต้องระบุคำค้นหา", // "Search query is required"
          fullQuery: req.query, // Return the full query for debugging purposes
        });
        return;
      }

      // Call the service to search for friends by username
      const friends = await searchFriendsByUsername(query);

      // If no friends found, return a 404 response
      if (friends.length === 0) {
        res.status(404).json({
          message: "ไม่พบเพื่อน", // "No friends found"
          query: query,
          details: "อาจเป็นเพราะไม่มีผู้ใช้ตรงกับคำค้นหา", // "It could be because no users match the search"
        });
        return;
      }

      // Return the found friends as a JSON response
      res.status(200).json({ friends });
    } catch (error) {
      // Handle any errors and pass them to the next error handler
      console.error("Error in searchFriends:", error);
      next(error);
    }
  }
);
