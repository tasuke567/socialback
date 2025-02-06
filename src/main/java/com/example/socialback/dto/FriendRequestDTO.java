package com.example.socialback.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FriendRequestDTO {
    private String fromUserId;
    private String toUserId;
}
