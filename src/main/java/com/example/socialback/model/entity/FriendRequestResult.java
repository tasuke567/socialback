package com.example.socialback.model.entity;

public enum FriendRequestResult {
    SENT,           // ✅ ส่งคำขอเรียบร้อย
    ALREADY_FRIENDS,// ⚡ เป็นเพื่อนกันแล้ว
    ALREADY_PENDING,// 📩 คำขอค้างอยู่
    INVALID_REQUEST // ❌ คำขอไม่ถูกต้อง
}
