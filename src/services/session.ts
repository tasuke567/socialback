// src/services/session.ts// Import Neo4j driver
import { Session } from "neo4j-driver";
import neo4j from "neo4j-driver";

// ตั้งค่าการเชื่อมต่อกับ Neo4j
const driver = neo4j.driver(
  "neo4j://localhost:7687",
  neo4j.auth.basic("neo4j", "password")
);

// Export the driver after declaration
export { driver }; // Export it here

// ฟังก์ชันที่ใช้จัดการ session กับฐานข้อมูล Neo4j
export const withSession = async (
  operation: (session: Session) => Promise<any>
): Promise<any> => {
  const session = driver.session();
  try {
    return await operation(session);
  } finally {
    await session.close();
  }
};

// ฟังก์ชันสำหรับตรวจสอบการเชื่อมต่อฐานข้อมูล
export const checkDatabaseConnection = async () => {
  const session = driver.session();
  try {
    await session.run("RETURN 1");
  } catch (error) {
    throw new Error("Unable to connect to the database");
  } finally {
    await session.close();
  }
};
