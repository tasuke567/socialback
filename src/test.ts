const jwt = require('jsonwebtoken');

// ข้อมูลผู้ใช้ที่จำเป็นสำหรับการสร้าง token
const user = {
  username: 'JohnDoe',   // ชื่อผู้ใช้
  userId: 'eab88173-7ace-42d6-a323-85919144b226',   // userId ของผู้ใช้
};

// สร้าง token โดยใช้ข้อมูลของผู้ใช้
const token = jwt.sign(user, 'your-secret-key', { expiresIn: '1h' });

// ส่ง token กลับไปยังฝั่งไคลเอ็นต์
console.log('Generated JWT:', token);
