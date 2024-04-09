package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangeIdType implements MessageType {
    private final String changeId;

    @Override
    public byte[] toBytes() {
        return changeId.getBytes();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        return new ChangeIdType(new String(bytes));
    }

}
