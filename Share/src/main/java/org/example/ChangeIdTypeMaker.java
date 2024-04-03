package org.example;

import lombok.Data;

@Data
public class ChangeIdTypeMaker implements TypeMaker{
    private final String changeId;
    private int typeNumber;
    public ChangeIdType make() {
        setTypeNumber(MessageTypeCode.CHANGE_ID.ordinal());
        return new ChangeIdType(changeId, typeNumber);
    }
}
