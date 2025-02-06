package com.example.socialback.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Node("User")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    private String profilePicture;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Email
    private String email;

    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Builder.Default
    private List<String> roles = List.of("ROLE_USER");

    @Builder.Default
    private Date createdAt = new Date();

    @Builder.Default
    private Date updatedAt = new Date();

    @Builder.Default
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Builder.Default
    private boolean enabled = true;

    // ความสัมพันธ์เพื่อน (UNDIRECTED)
    @Relationship(type = "FRIEND_WITH", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private Set<User> friends = new HashSet<>();

    // // ความสัมพันธ์คำขอเป็นเพื่อน
    // @Relationship(type = "FRIEND_REQUEST", direction = Relationship.Direction.OUTGOING)
    // @Builder.Default
    // private Set<FriendRequest> friendRequests = new HashSet<>();

    // // ฟังก์ชันจัดการเพื่อน
    // public void sendFriendRequest(User toUser) {
    //     FriendRequest friendRequest = new FriendRequest(this, toUser, FriendshipStatus.PENDING, new Date());
    //     friendRequests.add(friendRequest);
    // }

    // public void acceptFriendRequest(User fromUser) {
    //     friendRequests.removeIf(req -> req.getFromUser().equals(fromUser));
    //     friends.add(fromUser);
    //     fromUser.getFriends().add(this);
    // }

    // public void rejectFriendRequest(User fromUser) {
    //     friendRequests.removeIf(req -> req.getFromUser().equals(fromUser));
    // }

    public boolean isFriendWith(User user) {
        return friends.contains(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getUsername() {
        return username;
    }
}
