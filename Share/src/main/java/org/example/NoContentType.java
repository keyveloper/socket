package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class NoContentType implements Serializable, MessageType {
    private final String role;
}
