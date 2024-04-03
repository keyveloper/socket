package org.example;

import lombok.Data;

import java.net.Socket;

@Data
public class Message {
    private final int bodyLength;

    private final MessageTypeCode messageTypeCode;

    private final byte[] body;

    private final Socket clientSocket;
}
