// src/routes/postRoutes.ts
import express from "express";
import { createPost, getAllPosts, updatePost, deletePost } from "../controllers/postController";

const router = express.Router();

router.post("/", createPost);
router.get("/", getAllPosts);
router.put("/:id", updatePost);
router.delete("/:postId", deletePost);

export default router;
