package org.example;

public class FileNotSetException extends RuntimeException{
    public FileNotSetException(String message) {
        super(message);
    }
}
