// src/controllers/userController.ts
import { Request, Response } from "express";
import { withSession } from "../services/session"; // ใช้ session management
import { errorHandler } from "../utils/errorHandler"; // error handler

export const getUserProfile = async (req: Request, res: Response): Promise<void> => {
  try {
    const { userId } = req.params;

    if (!userId) {
      res.status(400).json({ message: "User ID is required." });
      return;
    }

    const userProfile = await withSession(async (session) => {
      const result = await session.run(
        `MATCH (u:User {id: $userId}) RETURN u`,
        { userId }
      );

      return result.records[0]?.get("u")?.properties;
    });

    if (!userProfile) {
      res.status(404).json({ message: "User not found." });
      return;
    }

    res.status(200).json({ userProfile });
  } catch (error) {
    errorHandler(error, res);
  }
};
