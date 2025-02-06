package com.example.socialback.config;

import java.time.LocalDateTime;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@ReadingConverter
public class Neo4jDateTimeToLocalDateTimeConverter implements Converter<Value, LocalDateTime> {
    @Override
    public LocalDateTime convert(Value source) {
        // ใช้ asLocalDateTime() หากเวอร์ชันของ driver รองรับ
        // หรือถ้า source เป็น org.neo4j.driver.types.ZonedDateTime คุณสามารถแปลงเป็น LocalDateTime ได้โดยการตัด zone ออก
        try {
            return source.asLocalDateTime();
        } catch (Exception e) {
            // หากไม่สามารถแปลงได้ ลองใช้การแปลงจาก ZonedDateTime
            return source.asZonedDateTime().toLocalDateTime();
        }
    }
}

