package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileType implements Serializable, MessageType{
    private final String receiver;
    private final String fileName;
    private final int seq;
    private final byte[] fileByte;
}
