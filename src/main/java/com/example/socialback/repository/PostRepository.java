package com.example.socialback.repository;

import com.example.socialback.model.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PostRepository extends Neo4jRepository<Post, Long> {
}