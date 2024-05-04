package org.example.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileStartInfo implements MessageType{
    private final String sender;
    private final String receiver;
    private final String filePath;
    private final UUID fileId;

    @Override
    public byte[] toBytes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileStartInfo fromBytes(byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new String(bytes), FileStartInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
