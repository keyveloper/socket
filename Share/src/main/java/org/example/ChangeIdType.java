package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangeIdType implements Serializable, MessageType {
    private final String changeId;
}
