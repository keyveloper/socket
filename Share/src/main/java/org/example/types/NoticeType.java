package org.example.types;

import lombok.Data;
import org.example.NoticeCode;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class NoticeType implements MessageType {
    private final NoticeCode noticeCode;
    private final String notice;
    @Override
    public byte[] toBytes() {
        int NOTICE_CODE_SIZE = 4;
        ByteBuffer buffer = ByteBuffer.allocate(NOTICE_CODE_SIZE + notice.length());
        buffer.putInt(noticeCode.ordinal());
        buffer.put(notice.getBytes());
        return buffer.array();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        NoticeCode extractedCode = NoticeCode.values() [buffer.getInt()];
        byte[] noticeBytes = new byte[buffer.remaining()];
        buffer.get(noticeBytes);

        return new NoticeType(extractedCode, new String(noticeBytes));

    }

}
