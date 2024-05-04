package org.example.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileEndType implements MessageType{
    // id = receiver, sender
    private final UUID tokenId;
    @Override
    public byte[] toBytes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileEndType fromBytes(byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new String(bytes), FileEndType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
