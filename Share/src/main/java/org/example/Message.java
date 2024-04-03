package org.example;

import lombok.Data;

@Data
public class Message {
    private final int bodyLength;

    private final MessageTypeCode messageType;

    private final byte[] body;
}
