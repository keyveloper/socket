package org.example;

public class NoExistCommandException extends RuntimeException{
    public NoExistCommandException(String message) {
        super(message);
    }
}
