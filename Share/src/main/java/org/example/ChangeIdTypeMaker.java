package org.example;

import lombok.Data;

@Data
public class ChangeIdTypeMaker implements TypeMaker{
    private final String changeId;
    public ChangeIdType make() {
        return new ChangeIdType(changeId);
    }
}
