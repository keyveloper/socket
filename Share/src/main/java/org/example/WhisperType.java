package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class WhisperType implements Serializable, MessageType {
    // id = receiver or sender
    private final String id;
    private final String comment;
}
