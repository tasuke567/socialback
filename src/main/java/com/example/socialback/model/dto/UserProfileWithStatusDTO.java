package com.example.socialback.model.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.example.socialback.model.entity.FriendshipStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileWithStatusDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private FriendshipStatus friendshipStatus; // new field

    public UserProfileWithStatusDTO(UserProfileDTO user, FriendshipStatus friendshipStatus) { 
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePicture = user.getProfilePicture();
        this.friendshipStatus = friendshipStatus;
    }


}
