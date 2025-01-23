import { driver } from '../database/neo4j';
import { User } from '../types/User';

export const searchFriendsByUsername = async (query: string, page: any = 1, limit: any = 10): Promise<User[]> => {
  const session = driver.session();

  try {
    // Ensure valid integer values for page and limit
    const parsedPage = Math.max(1, parseInt(page, 10) || 1);
    const parsedLimit = Math.max(1, Math.floor(Number(limit) || 10)); // Explicit conversion and floor for integer

    console.log('Search Query:', query);
    console.log('Page:', parsedPage, 'Limit:', parsedLimit);

    const result = await session.run(
      `
      MATCH (u:User)
      WHERE 
          toLower(u.name) CONTAINS $query OR 
          toLower(u.username) CONTAINS $query
      RETURN u
      `, 
      { query: query.toLowerCase() } // No pagination here
    );

    console.log('Number of results:', result.records.length);

    const friends: User[] = result.records
      .map((record) => {
        const userNode = record.get('u');
        return userNode ? (userNode.properties as User) : undefined;
      })
      .filter((user): user is User => user !== undefined);

    return friends;
  } catch (error: any) {
    console.error('Error searching friends:', error);
    throw new Error('An error occurred while searching for friends.');
  } finally {
    await session.close();
  }
};
