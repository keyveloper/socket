package org.example.types;

import lombok.Data;

@Data
public class NoContentType implements MessageType {
    private final String role;
    @Override
    public byte[] toBytes() {
        return role.getBytes();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        return new NoContentType(new String(bytes));
    }
}
