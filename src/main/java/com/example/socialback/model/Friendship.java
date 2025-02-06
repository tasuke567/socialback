package com.example.socialback.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.Date;

@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue
    private Long id;

    private FriendshipStatus status;

    @Builder.Default
    private Date createdAt = new Date();

    @Builder.Default
    private Date updatedAt = new Date();

    @TargetNode
    private User friend;

    public void updateStatus(FriendshipStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = new Date();
    }
}