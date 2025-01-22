import express, { Request, Response } from "express"; // Import Request and Response
import neo4j from "neo4j-driver";

const router = express.Router();
const driver = neo4j.driver(
  "neo4j://localhost:7687",
  neo4j.auth.basic("neo4j", "password")
);
// ตรวจสอบว่าเชื่อมต่อกับฐานข้อมูลได้หรือไม่
const checkDatabaseConnection = async () => {
  const session = driver.session();
  try {
    await session.run("RETURN 1");
  } catch (error) {
    throw new Error("Unable to connect to the database");
  } finally {
    await session.close();
  }
};

// เพิ่มความสัมพันธ์เพื่อน
router.post("/:userId/add-friend/:friendId", async (req: any, res: any) => {
  const { userId, friendId } = req.params;
  const session = driver.session();

  try {
    await checkDatabaseConnection(); 
    // ตรวจสอบว่าผู้ใช้ทั้งสองเป็นเพื่อนกันอยู่แล้วหรือไม่
    const result = await session.run(
      `
        MATCH (a:User {id: $userId})-[:FRIEND]->(b:User {id: $friendId})
        RETURN a, b
        `,
      { userId, friendId }
    );

    // หากพบความสัมพันธ์แล้ว
    if (result.records.length > 0) {
      return res.status(400).json({ message: "Already friends." });
    }

    // หากยังไม่มีความสัมพันธ์, สร้างความสัมพันธ์ใหม่
    const createFriendship = await session.run(
      `
        MATCH (a:User {id: $userId}), (b:User {id: $friendId})
        MERGE (a)-[:FRIEND]->(b)
        RETURN a, b
        `,
      { userId, friendId }
    );

    res.status(201).json({ message: "Friendship created successfully." });
  } catch (error: any) {
    console.error("Error adding friend:", error);
    res.status(500).json({
      error: error.message || "Internal Server Error",
      details: error.stack,
    });
  } finally {
    await session.close();
  }
});

// ดึงรายชื่อเพื่อน
router.get("/:userId", async (req: any, res: any) => {
  const { userId } = req.params;
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
    const friends = result.records.map(
      (record) => record.get("friend").properties
    );

    if (friends.length === 0) {
      return res.status(404).json({ message: "No friends found." });
    }

    res.status(200).json({ userId, friends });
  } catch (error) {
    console.error("Error fetching friends:", error);
    res.status(500).json({
      error: error instanceof Error ? error.message : "Unknown error occurred",
    });
  } finally {
    await session.close();
  }
});
// ลบความสัมพันธ์เพื่อน

router.delete("/:userId/unfriend/:friendId", async (req: any, res: any) => {
  const { userId, friendId } = req.params;
  console.log("User ID: ", userId);
  console.log("Friend ID: ", friendId);

  if (!userId || !friendId) {
    return res
      .status(400)
      .json({ error: "User ID and Friend ID are required." });
  }

  const session = driver.session();

  try {
    await checkDatabaseConnection(); 
    const result = await session.run(
      `MATCH (a:User {id: $userId})-[r:FRIEND]->(b:User {id: $friendId})
         RETURN r`,
      { userId, friendId }
    );

    if (result.records.length === 0) {
      return res.status(404).json({ error: "Friendship not found." });
    }

    await session.run(
      `MATCH (a:User {id: $userId})-[r:FRIEND]->(b:User {id: $friendId})
         DELETE r`,
      { userId, friendId }
    );
    res.setHeader('Content-Type', 'application/json');
    res.status(200).json({ message: "Friendship removed successfully." });
  } catch (error: any) {
    console.error("Error:", error.message);
    res.status(500).json({ error: error.message });
  } finally {
    await session.close();
  }
});

// เพิ่มความสัมพันธ์ BLOCKED
router.post("/:userId/block/:friendId", async (req, res) => {
  const { userId, friendId } = req.params;
  const session = driver.session();
  try {
    await checkDatabaseConnection(); 
    const result = await session.run(
      `MATCH (a:User {id: $userId}), (b:User {id: $friendId})
         MERGE (a)-[:BLOCKED]->(b)`,
      { userId, friendId }
    );
    res.status(201).send("User blocked successfully.");
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  } finally {
    await session.close();
  }
});
// ส่งคำขอเป็นเพื่อน
router.post("/:userId/request-friend/:friendId", async (req, res) => {
  const { userId, friendId } = req.params;
  const session = driver.session();
  try {
    await checkDatabaseConnection(); 
    const result = await session.run(
      `MATCH (a:User {id: $userId}), (b:User {id: $friendId})
         MERGE (a)-[:REQUESTED]->(b)`,
      { userId, friendId }
    );
    res.status(201).send("Friend request sent.");
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  } finally {
    await session.close();
  }
});

export default router;
