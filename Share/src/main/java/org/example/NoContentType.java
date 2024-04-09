package org.example;

import lombok.Data;

import java.io.Serializable;

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
