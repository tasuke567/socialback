package com.example.socialback.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Node("User")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Getter
    private Long id;  // Mapped to Neo4j's internal elementId

    @Property(name = "firstName")
    @Getter @Setter
    private String firstName;

    @Property(name = "lastName")
    @Getter @Setter
    private String lastName;

    @Property(name = "profilePicture")
    @Getter @Setter
    private String profilePicture;

    @Property(name = "username")
    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private List<String> roles = List.of("ROLE_USER");

    @Getter @Setter
    private Date createdAt;

    @Getter @Setter
    private Date updatedAt;

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Implement methods from UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;  // หรือปรับตามเงื่อนไขของระบบคุณ
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // หรือปรับตามเงื่อนไขของระบบคุณ
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // หรือปรับตามเงื่อนไขของระบบคุณ
    }

    @Override
    public boolean isEnabled() {
        return true;  // หรือปรับตามเงื่อนไขของระบบคุณ
    }
}
