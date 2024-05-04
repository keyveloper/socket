package org.example.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.NoticeCode;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NoticeType implements MessageType {
    private final NoticeCode noticeCode;
    private final String notice;
    @Override
    public byte[] toBytes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static NoticeType fromBytes(byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new String(bytes), NoticeType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
