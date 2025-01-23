// src/types/express.d.ts
declare global {
    namespace Express {
      interface Request {
        user: {
          id: string; // หรือประเภทที่เหมาะสม
          username: string;
          // เพิ่มฟิลด์ที่คุณต้องการ
        };
      }
    }
  }
  
  export {};
  