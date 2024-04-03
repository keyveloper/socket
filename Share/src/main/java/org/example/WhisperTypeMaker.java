package org.example;

import lombok.Data;

@Data
public class WhisperTypeMaker implements TypeMaker{
    private final String receiver;
    private final String comment;
    private int typeNumber;
    public WhisperType make() {
        setTypeNumber(MessageTypeLibrary.WHISPER.ordinal());
        return new WhisperType(receiver, comment);
    }
}
