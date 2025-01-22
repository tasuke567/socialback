import express from 'express';
import { searchUsers } from '../controllers/userController';

const router = express.Router();

// เพิ่มการใช้ฟังก์ชัน searchUsers ใน route
router.get('/search', async (req, res) => {
  try {
    await searchUsers(req, res); // เรียกใช้ฟังก์ชันที่กำหนดใน controller
  } catch (error) {
    res.status(500).json({ message: 'Internal server error', error });
  }
});

export default router;
