package org.example;

import lombok.Data;

@Data
public class RegisterIdTypeMaker implements TypeMaker{
    private final String id;
    @Override
    public RegisterIdType make() {
        return new RegisterIdType(id);
    }
}
