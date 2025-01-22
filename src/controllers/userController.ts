import { Request, Response } from 'express';
import { session } from '../database/neo4j';

export const searchUsers = async (req: Request, res: Response) => {
  const query = req.query.query as string;

  if (!query) {
    return res.status(400).json({ message: 'Query parameter is required.' });
  }

  try {
    const result = await session.run(
      `MATCH (user:User)
       WHERE user.name CONTAINS $query OR user.email CONTAINS $query
       RETURN user`,
      { query }
    );

    const users = result.records.map(record => record.get('user').properties);

    if (users.length === 0) {
      return res.status(404).json({ message: 'No users found.' });
    }

    res.status(200).json({ users });
  } catch (error: any) {
    console.error('Error searching users:', error);
    res.status(500).json({ message: 'Error searching users', error: error.message });
  }
};
