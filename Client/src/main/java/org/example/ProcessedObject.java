package org.example;

import lombok.Data;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

@Data
public class ProcessedObject {
    private final MessageTypeCode messageTypeCode;
    private final MessageType messageType;
}

