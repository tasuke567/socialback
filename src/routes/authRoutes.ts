// authRoutes.ts
import { Router, Request, Response } from 'express';
import { registerUser, loginUser } from '../controllers/authController';

const router: Router = Router();

router.post('/register', async (req: Request, res: Response) => {
  try {
    await registerUser(req, res);
  } catch (error) {
    console.error('Unexpected error in register route:', error);
    res.status(500).json({ 
      message: 'An unexpected error occurred during registration' 
    });
  }
});

router.post('/login', async (req: Request, res: Response) => { 
  try {
    await loginUser(req, res);
  } catch (error) {
    console.error('Unexpected error in login route:', error);
    res.status(500).json({ 
      message: 'An unexpected error occurred during login' 
    });
  }
});

export default router;