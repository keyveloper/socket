package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileType implements MessageType {
    private final String sender;
    private final String receiver;
    private final String fileName;
    private final int seq;
    private final byte[] fileByte;
    private String filePath;
}
