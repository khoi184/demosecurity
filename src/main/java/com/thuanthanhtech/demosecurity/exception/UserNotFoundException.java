package com.thuanthanhtech.demosecurity.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String email) {
        super("Not found any user for email or username: " + email);
    }
}
