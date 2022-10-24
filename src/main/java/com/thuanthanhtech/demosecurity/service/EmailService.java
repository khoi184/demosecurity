package com.thuanthanhtech.demosecurity.service;

import com.thuanthanhtech.demosecurity.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException;

    void sendOtpEmail(User user, Integer OTP) throws UnsupportedEncodingException, MessagingException;

    Integer generateOneTimePassword(User user) throws UnsupportedEncodingException, MessagingException;

    void clearOTP(User user);
}