package com.example.socialback.model.dao;

import com.example.socialback.model.dto.PostDTO;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PostRowMapper implements RowMapper<PostDTO> {
    @Override
    public PostDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostDTO(
                rs.getObject("id", UUID.class),
                rs.getObject("owner_id", UUID.class),
                rs.getString("title"),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getString("username")
        );
    }
}
