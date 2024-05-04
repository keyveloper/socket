package org.example.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileType implements MessageType {
    private final String sender;
    private final String receiver;
    private final String fileName;
    private final int seq;
    private final byte[] fileByte;
    private String filePath;
    @Override
    public byte[] toBytes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileType fromBytes(byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new String(bytes), FileType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
