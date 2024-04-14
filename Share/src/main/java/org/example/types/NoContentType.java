package org.example.types;

import lombok.Data;

@Data
public class NoContentType implements MessageType {
    private final String role;
    // 이 클래스는 그냥 없애도 되는거 아님?
    @Override
    public byte[] toBytes() {
        return role.getBytes();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        return new NoContentType(new String(bytes));
    }
}
