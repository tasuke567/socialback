package com.example.socialback.repository;

import com.example.socialback.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FriendRepository extends Neo4jRepository<User, UUID> {

    @Query("MATCH (u:User {id: $userId})-[:FRIEND_WITH]-(friend:User) RETURN friend")
    List<User> findAllFriends(@Param("userId") UUID id);

    @Query("MATCH (u1:User {id: $userId1})-[:FRIEND_WITH]-(u2:User {id: $userId2}) RETURN COUNT(u2) > 0")
    boolean areFriends(@Param("userId1") UUID id1, @Param("userId2") UUID id2);

    @Query("MATCH (u1:User {id: $userId1}), (u2:User {id: $userId2}) " +
            "MERGE (u1)-[:FRIEND_WITH]-(u2)")
    void createFriendship(@Param("userId1") UUID id1, @Param("userId2") UUID id2);

    @Query("MATCH (u1:User {id: $userId1})-[r:FRIEND_WITH]-(u2:User {id: $userId2}) DELETE r")
    void removeFriendship(@Param("userId1") UUID id1, @Param("userId2") UUID id2);
}
