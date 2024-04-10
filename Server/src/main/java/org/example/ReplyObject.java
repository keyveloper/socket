package org.example;

import lombok.Data;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

@Data
public class ReplyObject {
    MessageTypeCode messageTypeCode;
    MessageType messageType;
}
