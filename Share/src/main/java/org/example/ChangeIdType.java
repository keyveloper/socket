package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangeIdType implements MessageType {
    private final String changeId;
}
