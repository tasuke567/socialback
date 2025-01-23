import { Schema, model, Document } from 'mongoose';  // Example using Mongoose

interface Friend extends Document {
  userId: string;
  friendId: string;
  createdAt: Date;
  updatedAt: Date;
}

const friendSchema = new Schema<Friend>({
  userId: { type: String, required: true },
  friendId: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now },
});

const FriendModel = model<Friend>('Friend', friendSchema);

export default FriendModel;
