package org.example;

import lombok.Data;

@Data
public class RegisterIdStatusTypeMaker implements TypeMaker{
    private final Boolean isSuccess;
    private String refuseReason;
    @Override
    public RegisterIdStatusType make() {
        RegisterIdStatusType registerIdStatusType = new RegisterIdStatusType(isSuccess);
        if (!isSuccess) {
            registerIdStatusType.setNotice(refuseReason);
        }
        return new RegisterIdStatusType(isSuccess);
    }
}
