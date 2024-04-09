package org.example;

import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Data
public class RegisterIdStatusType implements MessageType{
    private final Boolean isSuccess;
    private final String notice;
    @Override
    public byte[] toBytes() {
        int BOOLEAN_SIZE = 1;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BOOLEAN_SIZE + notice.length());
        byteBuffer.put((byte) (isSuccess ? 1 : 0));
        byteBuffer.put(notice.getBytes());

        return byteBuffer.array();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte isSuccessByte = byteBuffer.get();
        boolean isSuccess = isSuccessByte != 0;
        byte[] noticeBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(noticeBytes);

        return new RegisterIdStatusType(isSuccess, new String(noticeBytes));
    }
}
