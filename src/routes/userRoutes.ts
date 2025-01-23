// src/routes/userRoutes.ts
import express from "express";
import { getUserProfile } from "../controllers/userController";
import { searchFriends } from "../controllers/friendsController";
import asyncHandler from "../utils/asyncHandler";

const router = express.Router();

router.get("/:userId", getUserProfile);
router.get('/find', asyncHandler(searchFriends));

export default router;
