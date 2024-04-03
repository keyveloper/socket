package org.example;

import lombok.Data;

@Data
public class Message {
    private final int bodyLength;

    private final MessageTypeLibrary messageType;

    private final byte[] body;
}
