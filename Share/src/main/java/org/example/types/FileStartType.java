package org.example.types;

import lombok.Data;

@Data
public class FileStartType implements MessageType {
    private final String receiver;
    private final String fileName;

    // only for Sender Client
    private String filePath;

    @Override
    public byte[] toBytes() {
        return null;
    }

    @Override
    public FileStartType fromBytes(byte[] bytes) {
        return new FileStartType("test", "test");
    }
}
