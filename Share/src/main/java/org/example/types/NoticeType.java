package org.example.types;

import lombok.Data;

@Data
public class NoticeType implements MessageType {
    private final String notice;
    @Override
    public byte[] toBytes() {
        return notice.getBytes();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        return new NoticeType(new String(bytes));
    }

}
