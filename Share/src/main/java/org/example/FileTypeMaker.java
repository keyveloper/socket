package org.example;

import lombok.Data;

@Data
public class FileTypeMaker implements TypeMaker{
    private final String receiver;
    private final Object file;

    public FileType make() {
        return new FileType(receiver, file);
    }
}
