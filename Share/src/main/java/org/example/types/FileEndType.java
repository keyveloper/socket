package org.example.types;

import lombok.Data;

@Data
public class FileEndType implements MessageType{
    private final String receiver;
    private final String fileName;

    @Override
    public byte[] toBytes() {
        return null;
    }

    @Override
    public FileEndType fromBytes(byte[] bytes) {
        return null;
    }
}
