package org.example;

import lombok.Data;

@Data
public class ProcessedObject {
    private final MessageTypeCode messageTypeCode;
    private final MessageType messageType;
}

