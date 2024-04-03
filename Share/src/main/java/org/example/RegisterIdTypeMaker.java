package org.example;

import lombok.Data;

@Data
public class RegisterIdTypeMaker implements TypeMaker {
    private final String id;
    private int typeNumber;

    @Override
    public RegisterIdType make() {
        setTypeNumber(MessageTypeCode.REGISTER_ID.ordinal());
        return new RegisterIdType(id, typeNumber);
    }
}
