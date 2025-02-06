package com.example.socialback.repository;

import com.example.socialback.model.FriendRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends Neo4jRepository<FriendRequest, UUID> {

    @Query("""
            MATCH (u1:User {id: $fromUserId})-[r:SENT_REQUEST]->(req:FriendRequest)-[r2:RECEIVED_REQUEST]->(u2:User {id: $toUserId})
            WHERE req.status = 'PENDING'
            RETURN req
            """)
    Optional<FriendRequest> findFriendRequest(
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );

    @Query("""
            MATCH (fromUser:User {id: $fromUserId}), (toUser:User {id: $toUserId})
            CREATE (req:FriendRequest {
                id: randomUUID(),
                status: 'PENDING',
                createdAt: datetime(),
                fromUser: $fromUserId,
                toUser: $toUserId
            })
            CREATE (fromUser)-[:SENT_REQUEST]->(req)
            CREATE (req)-[:RECEIVED_REQUEST]->(toUser)
            RETURN req
            """)
    FriendRequest createFriendRequest(
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );

    @Query("""
            MATCH (u1:User {id: $fromUserId})-[r:SENT_REQUEST]->(req:FriendRequest)-[r2:RECEIVED_REQUEST]->(u2:User {id: $toUserId})
            DETACH DELETE req
            """)
    void deleteFriendRequest(
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );


    @Query("""
                MATCH (fromUser:User {id: $fromUserId}), (toUser:User {id: $toUserId})
                CREATE (req:FriendRequest {
                    id: $requestId,
                    status: 'PENDING',
                    createdAt: datetime(),
                    fromUser: $fromUserId,
                    toUser: $toUserId
                })
                CREATE (fromUser)-[:SENT_REQUEST]->(req)
                CREATE (req)-[:RECEIVED_REQUEST]->(toUser)
                RETURN req
            """)
    FriendRequest createFriendRequest(
            @Param("requestId") UUID requestId,
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );

    @Query("""
                MATCH (u1:User {id: $userId1}), (u2:User {id: $userId2})
                CREATE (u1)-[f:FRIEND_WITH {
                    createdAt: datetime(),
                    status: 'ACCEPTED'
                }]->(u2)
                RETURN f
            """)
    void createFriendship(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);


    @Query("""
    MATCH (u1:User {id: $fromUserId})-[:SENT_REQUEST]->(req:FriendRequest)-[:RECEIVED_REQUEST]->(u2:User {id: $toUserId})
    WHERE req.status = 'PENDING'
    RETURN COUNT(req) > 0
    """)
    boolean existsByFromUserAndToUser(
            @Param("fromUserId") UUID fromUserId,
            @Param("toUserId") UUID toUserId
    );
}
