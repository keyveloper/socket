package org.example;

public class IncorrectCommandException extends RuntimeException{
    public IncorrectCommandException(String message) {
        super(message);
    }
}
