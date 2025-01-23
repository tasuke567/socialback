import express, { Application, NextFunction } from "express";
import cors from "cors";
import userRoutes from "./routes/userRoutes";
import friendsRoutes from "./routes/friendRoutes";
import authRoutes from "./routes/authRoutes";
import postsRoutes from "./routes/postRoutes";
import friendRoutes from "./routes/friendRoutes";

const app: Application = express();
const PORT: number = 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use("/api/auth", authRoutes);
app.use("/api/user",userRoutes);
app.use("/api/friends", friendsRoutes); // รวมเส้นทางเพื่อน
app.use("/api/posts",postsRoutes);



// Swagger
const swaggerJSDoc = require("swagger-jsdoc");
const swaggerUi = require("swagger-ui-express");

// Swagger setup
const swaggerOptions = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "Social Network API",
      version: "1.0.0",
      description: "API documentation for the Social Network app",
    },
  },
  apis: ["./src/**/*.js"], // Ensure this path is correct
};

const swaggerSpec = swaggerJSDoc(swaggerOptions);

app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));
console.log(swaggerSpec);

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});

export default app;
