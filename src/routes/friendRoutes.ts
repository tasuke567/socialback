// src/routes/friendsRoutes.ts
import express from "express";
import { addFriend, getFriends, searchFriends, unfriend } from "../controllers/friendsController";
import asyncHandler from "../utils/asyncHandler";

const router = express.Router();

// Add a friend
router.post("/:userId/add-friend/:friendId", asyncHandler(addFriend));

// Get friends list
router.get("/:userId", asyncHandler(getFriends));

// Remove a friend
router.delete("/:userId/unfriend/:friendId", asyncHandler(unfriend));

// Search friends by query


export default router;
