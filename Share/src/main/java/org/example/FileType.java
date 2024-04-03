package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileType implements Serializable, MessageType{
    private final String receiver;
    private final Object file;
    private final int TypeNumber;
}
