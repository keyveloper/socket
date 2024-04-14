package org.example;

import lombok.Data;
import org.example.types.MessageTypeCode;

import java.net.Socket;

@Data
public class Message {

    private final MessageTypeCode messageTypeCode;

    private final byte[] body;

    private final Socket sender;
}
