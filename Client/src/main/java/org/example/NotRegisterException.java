package org.example;

public class NotRegisterException extends RuntimeException{
    public NotRegisterException() {
        super("Register First\n /r or /R your ID");
    }

}
