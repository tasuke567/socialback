package com.example.socialback.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Node("FriendRequest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @Property("fromUser")
    private UUID fromUser;

    @Property("toUser")
    private UUID toUser;

    @Property("status")
    private FriendshipStatus status;

    @Property("createdAt")
    private ZonedDateTime createdAt; // ✅ ใช้ ZonedDateTime ตรงกับ Neo4j

    // Constructor ที่รับ User
    public FriendRequest(User fromUser, User toUser, FriendshipStatus status, Date createdAt) {
        this.fromUser = fromUser.getId();
        this.toUser = toUser.getId();
        this.status = status;
        this.createdAt = createdAt.toInstant().atZone(ZoneId.systemDefault()); // ✅ แปลง Date → ZonedDateTime
    }

    // Constructor ที่รับ UUID
    public FriendRequest(UUID fromUser, UUID toUser, FriendshipStatus status) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
        this.createdAt = ZonedDateTime.now(); // ✅ ใช้ ZonedDateTime
    }

    // Constructor ที่รับเฉพาะ Status
    public FriendRequest(FriendshipStatus status) {
        this.status = status;
        this.createdAt = ZonedDateTime.now();
    }
}
