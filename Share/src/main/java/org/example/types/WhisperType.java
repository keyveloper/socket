package org.example.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
;
import java.nio.ByteBuffer;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class WhisperType implements MessageType {
    // id = receiver or sender
    private final String id;
    private final String comment;

    @Override
    public byte[] toBytes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static WhisperType fromBytes(byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new String(bytes), WhisperType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
