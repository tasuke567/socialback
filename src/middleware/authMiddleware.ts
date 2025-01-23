import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';

// ตัวอย่าง middleware สำหรับการตรวจสอบ token
const authenticateToken = (req: Request, res: Response, next: NextFunction) => {
  const token = req.headers['authorization'];

  if (!token) return res.status(403).json({ message: 'No token provided' });

  jwt.verify(token, process.env.JWT_SECRET as string, (err, user) => {
    if (err) return res.status(403).json({ message: 'Invalid token' });

    // ตรวจสอบว่า user ไม่เป็น undefined
    if (user) {
      req.user = user as { id: string; username: string }; // การกำหนดประเภท
      next();
    } else {
      return res.status(403).json({ message: 'Invalid token' });
    }
  });
};

export default authenticateToken;
