package org.example.types;

import lombok.Data;

@Data
public class RegisterIdType implements MessageType {
    private final String id;
    @Override
    public byte[] toBytes() {
        return id.getBytes();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        return new RegisterIdType(new String(bytes));
    }
}
