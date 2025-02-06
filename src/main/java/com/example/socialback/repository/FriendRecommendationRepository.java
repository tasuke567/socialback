// FriendRecommendationRepository.java
package com.example.socialback.repository;

import com.example.socialback.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendRecommendationRepository extends Neo4jRepository<User, UUID> {

    // หาเพื่อนของเพื่อน (Friend of Friends)
    @Query("""
        MATCH (user:User)-[:FRIEND_WITH]->(friend:User)-[:FRIEND_WITH]->(fof:User)
        WHERE user.id = $userId 
        AND NOT (user)-[:FRIEND_WITH]->(fof)
        AND user <> fof
        RETURN DISTINCT fof, COUNT(friend) as mutualFriends
        ORDER BY mutualFriends DESC
        LIMIT 10
    """)
    List<User> findFriendOfFriends(@Param("userId") UUID userId);

    // หาคนที่มีความสนใจคล้ายกัน
    @Query("""
        MATCH (user:User), (other:User)
        WHERE user.id = $userId 
        AND NOT (user)-[:FRIEND_WITH]->(other)
        AND user <> other
        AND any(interest IN user.interests WHERE interest IN other.interests)
        RETURN other, 
        size([i IN user.interests WHERE i IN other.interests]) as commonInterests
        ORDER BY commonInterests DESC
        LIMIT 10
    """)
    List<User> findUsersWithCommonInterests(@Param("userId") UUID userId);
}