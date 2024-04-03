package org.example;

import lombok.Data;

@Data
public class WhisperTypeMaker implements TypeMaker{
    private final String receiver;
    private final String comment;
    public WhisperType make() {
        return new WhisperType(receiver, comment);
    }
}
