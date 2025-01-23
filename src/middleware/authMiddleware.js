"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
// ตัวอย่าง middleware สำหรับการตรวจสอบ token
const authenticateToken = (req, res, next) => {
    const token = req.headers['authorization'];
    if (!token)
        return res.status(403).json({ message: 'No token provided' });
    jsonwebtoken_1.default.verify(token, process.env.JWT_SECRET, (err, user) => {
        if (err)
            return res.status(403).json({ message: 'Invalid token' });
        // ตรวจสอบว่า user ไม่เป็น undefined
        if (user) {
            req.user = user; // การกำหนดประเภท
            next();
        }
        else {
            return res.status(403).json({ message: 'Invalid token' });
        }
    });
};
exports.default = authenticateToken;
