// authController.ts
import { Request, Response } from "express";
import bcrypt from "bcryptjs";
import { driver } from "../database/neo4j";
import { v4 as uuidv4 } from "uuid";
import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';
dotenv.config();

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key';

interface User {
  id: string;
  name: string;
  email: string;
  password: string;
}

export const registerUser = async (req: Request, res: Response) => {
  const { name, email, password } = req.body;
  const session = driver.session();

  try {
    // Validate input
    if (!name?.trim() || !email?.trim() || !password?.trim()) {
      return res.status(400).json({ message: "All fields are required." });
    }

    // Check if user already exists
    const existingUser = await session.run(
      `MATCH (u:User {email: $email}) RETURN u`,
      { email }
    );

    if (existingUser.records.length > 0) {
      return res.status(409).json({ message: "Email already registered." });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 12);
    const userId = uuidv4();

    // Create user
    const result = await session.run(
      `CREATE (u:User {
        id: $id,
        name: $name, 
        email: $email, 
        password: $password,
        createdAt: datetime()
      }) RETURN u`,
      { 
        id: userId,
        name, 
        email, 
        password: hashedPassword 
      }
    );

    const user = result.records[0].get("u").properties as User;
    
    // Create JWT token
    const token = jwt.sign(
      { userId: user.id },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    return res.status(201).json({ 
      message: "User registered successfully",
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email
      }
    });

  } catch (error) {
    console.error("Error during registration:", error);
    return res.status(500).json({ message: "Error during registration" });
  } finally {
    await session.close();
  }
};

export const loginUser = async (req: Request, res: Response) => {
  const { email, password } = req.body;
  const session = driver.session();

  try {
    // Validate input
    if (!email?.trim() || !password?.trim()) {
      return res.status(400).json({ message: "Email and password are required." });
    }

    // Find user
    const result = await session.run(
      `MATCH (user:User {email: $email}) RETURN user`,
      { email }
    );

    const user = result.records[0]?.get("user")?.properties as User | undefined;

    if (!user) {
      return res.status(401).json({ message: "Invalid credentials." });
    }

    // Verify password
    const isPasswordValid = await bcrypt.compare(password, user.password);

    if (!isPasswordValid) {
      return res.status(401).json({ message: "Invalid credentials." });
    }

    // Generate JWT token
    const token = jwt.sign(
      { userId: user.id },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    return res.status(200).json({
      message: "Login successful",
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email
      }
    });

  } catch (error) {
    console.error("Error during login:", error);
    return res.status(500).json({ message: "Internal Server Error" });
  } finally {
    await session.close();
  }
};