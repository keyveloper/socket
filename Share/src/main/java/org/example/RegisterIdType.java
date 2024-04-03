package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterIdType implements Serializable, MessageType {
    private final String id;
    private final int TypeNumber;
}
