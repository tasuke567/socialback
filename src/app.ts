import express, { Application } from 'express';
import cors from 'cors';
import userRoutes from './routes/userRoutes';
import friendRoutes from './routes/friendRoutes';
import authRoutes from './routes/authRoutes';
import postsRoutes from './routes/postRoutes';

const app: Application = express();
const PORT: number = 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/friends', friendRoutes);
app.use('/api/', postsRoutes); 


app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});

export default app;