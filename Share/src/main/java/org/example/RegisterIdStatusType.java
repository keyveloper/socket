package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterIdStatusType implements Serializable, MessageType{
    private final Boolean isSuccess;
    private String notice;
}
