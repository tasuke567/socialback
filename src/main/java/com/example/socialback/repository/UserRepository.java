package com.example.socialback.repository;

import com.example.socialback.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Neo4jRepository<User, UUID> {

    // Custom query to find user by email and return User details
    @Query("MATCH (user:`User`) WHERE user.email = $email RETURN user")
    Optional<User> findByEmail(String email);

    // Custom query to find user by username and return User details with specific fields
    @Query("MATCH (user:`User`) WHERE user.username = $username RETURN user{.id, .createdAt, .email, .firstName, .lastName, .password, .profilePicture, .roles, .updatedAt, .username, __nodeLabels__: labels(user), __internalNeo4jId__: ID(user), __elementId__: ID(user)}")
    Optional<User> findByUsername(String username);


}
