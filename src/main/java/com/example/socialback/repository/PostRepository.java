package com.example.socialback.repository;

import com.example.socialback.model.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface PostRepository extends Neo4jRepository<Post, UUID> {
}