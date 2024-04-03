package org.example;

import lombok.Data;

@Data
public class FileTypeMaker implements TypeMaker{
    private final String receiver;
    private final Object file;
    private int typeNumber;

    @Override
    public FileType make() {
        setTypeNumber(MessageTypeLibrary.FILE.ordinal());
        return new FileType(receiver, file, typeNumber);
    }

}
