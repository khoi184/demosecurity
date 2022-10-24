package com.thuanthanhtech.demosecurity.exception;

public class FailOTPException extends Exception{
    public FailOTPException() {
        super("Entered Otp is NOT valid. Please Retry!");
    }
}
