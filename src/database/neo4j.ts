import neo4j from 'neo4j-driver';
import * as dotenv from 'dotenv';
dotenv.config();

// สร้างการเชื่อมต่อกับฐานข้อมูล Neo4j
const driver = neo4j.driver(
    process.env.NEO4J_URL || 'neo4j://localhost:7687', 
    neo4j.auth.basic(process.env.NEO4J_USERNAME || 'neo4j', process.env.NEO4J_PASSWORD || 'password') // ใส่ username และ password ของฐานข้อมูล Neo4j
);

// เปิด session ที่จะใช้ในการดำเนินการกับฐานข้อมูล
const session = driver.session();

// ฟังก์ชันสำหรับปิดการเชื่อมต่อ
const closeConnection = async () => {
  await session.close();
  await driver.close();
};

export { driver, session, closeConnection };
