package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class RegisterIdStatusType implements MessageType {
    private final Boolean isSuccess;
    private final String registerId;
    private final String notice;
    @Override
    public byte[] toBytes() {
        int BOOLEAN_SIZE = 1;
        int ID_SIZE = 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BOOLEAN_SIZE + ID_SIZE + registerId.length() + notice.length());
        byteBuffer.put((byte) (isSuccess ? 1 : 0));
        byteBuffer.putInt(registerId.length());
        byteBuffer.put(registerId.getBytes());
        byteBuffer.put(notice.getBytes());

        return byteBuffer.array();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte isSuccessByte = byteBuffer.get();
        boolean isSuccess = isSuccessByte != 0;

        int registerIdLength = byteBuffer.getInt();
        byte[] registerIdBytes = new byte[registerIdLength];
        byteBuffer.get(registerIdBytes);

        byte[] noticeBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(noticeBytes);

        return new RegisterIdStatusType(isSuccess, new String(registerIdBytes), new String(noticeBytes));
    }
}
