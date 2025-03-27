package com.example.socialback.model.entity;

public enum FriendshipStatus {
    PENDING,   // 📩 รอการยืนยัน
    ACCEPTED,  // ✅ เป็นเพื่อนกันแล้ว
    REJECTED,  // ❌ ถูกปฏิเสธ
    BLOCKED  ,  // 🚫 ถูกบล็อค
    NONE        // ❌ ไม่มีความสัมพันธ์
}
