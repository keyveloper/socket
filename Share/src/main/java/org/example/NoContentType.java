package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class NoContentType implements Serializable {
    private final String role;
}
