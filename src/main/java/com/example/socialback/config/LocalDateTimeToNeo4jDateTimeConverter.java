package com.example.socialback.config;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDateTime;

@WritingConverter
public class LocalDateTimeToNeo4jDateTimeConverter implements Converter<LocalDateTime, Value> {
    @Override
    public Value convert(LocalDateTime source) {
        return Values.value(source);
    }
}
