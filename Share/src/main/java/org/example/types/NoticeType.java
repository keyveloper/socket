package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.NoticeCode;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NoticeType implements MessageType {
    private final NoticeCode noticeCode;
    private final String notice;

}
