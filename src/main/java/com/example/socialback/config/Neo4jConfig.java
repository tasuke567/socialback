package com.example.socialback.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.example.socialback.repository")
@EnableNeo4jAuditing
public class Neo4jConfig {

    @Bean
    public Driver neo4jDriver(
            @Value("${spring.neo4j.uri}") String uri,
            @Value("${spring.neo4j.authentication.username}") String username,
            @Value("${spring.neo4j.authentication.password}") String password
    ) {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Bean
    public Neo4jTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }

    @Bean
    public Neo4jConversions neo4jConversions() {
        return new Neo4jConversions();
    }

    @Bean
    public Neo4jMappingContext neo4jMappingContext(Neo4jConversions conversions) {
        return new Neo4jMappingContext(conversions);
    }
}
