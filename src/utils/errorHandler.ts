// src/utils/errorHandler.ts
import { Response } from "express";

// ฟังก์ชันสำหรับจัดการ error และส่งข้อความ error กลับไปยัง client
export const errorHandler = (error: any, res: Response): void => {
  console.error("Error:", error);
  const status = error.status || 500;
  const message = error.status ? error.message : "Internal Server Error";
  res.status(status).json({ message, error: error.message });
};
