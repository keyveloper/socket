package org.example;

import lombok.Data;

@Data
public class Message {
    private final int bodyLength;

    private final MessageType messageType;

    private final byte[] body;
}
