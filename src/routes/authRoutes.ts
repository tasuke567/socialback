import { Router, Request, Response } from 'express';
import { registerUser, loginUser } from '../controllers/authController';

const router: Router = Router();

/**
 * @swagger
 * /api/auth/register:
 *   post:
 *     description: Register a new user
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               username:
 *                 type: string
 *               email:
 *                 type: string
 *                 format: email
 *               password:
 *                 type: string
 *             required:
 *               - username
 *               - email
 *               - password
 *     responses:
 *       201:
 *         description: User successfully registered
 *       400:
 *         description: Bad request, missing or incorrect parameters
 *       500:
 *         description: Unexpected error during registration
 */
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

/**
 * @swagger
 * /api/auth/login:
 *   post:
 *     description: Login with existing user credentials
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               email:
 *                 type: string
 *                 format: email
 *               password:
 *                 type: string
 *             required:
 *               - email
 *               - password
 *     responses:
 *       200:
 *         description: User successfully logged in
 *       401:
 *         description: Unauthorized, invalid credentials
 *       500:
 *         description: Unexpected error during login
 */
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
