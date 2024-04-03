package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class WhisperType implements Serializable, MessageType {
    private final String receiver;
    private final String comment;
}
