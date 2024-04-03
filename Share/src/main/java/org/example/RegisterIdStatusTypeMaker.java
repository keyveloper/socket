package org.example;

import lombok.Data;

@Data
public class RegisterIdStatusTypeMaker implements TypeMaker{
    private final Boolean isSuccess;
    private String refuseReason;
    private int typeNumber;
    @Override
    public RegisterIdStatusType make() {
        setTypeNumber(MessageTypeLibrary.REGISTER_STATUS.ordinal());
        RegisterIdStatusType registerIdStatusType = new RegisterIdStatusType(isSuccess, typeNumber);
        if (!isSuccess) {
            registerIdStatusType.setNotice(refuseReason);
        }
        return new RegisterIdStatusType(isSuccess, typeNumber);
    }
}
