package com.thuanthanhtech.demosecurity.exception;

public class UsernameExistedException extends RuntimeException{
    public UsernameExistedException() {
        super("Username is existed!");
    }
}
