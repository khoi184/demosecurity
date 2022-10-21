package com.thuanthanhtech.demosecurity.service;

import com.thuanthanhtech.demosecurity.dto.SignUpDto;
import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.exception.FailOTPException;
import com.thuanthanhtech.demosecurity.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUserId(Long id);

    User findByUsername(String username);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    void changeAccountPassword(User user, String password);

    String forgotPassword(String email);

    String resetPassword(String token, String password);

    User findByToken(String token);

    void updateResetPasswordToken(String token, String email) throws UserNotFoundException;

    void registerUser(SignUpDto signUpDto);

    void validateOTP(Integer otpNumber, String email) throws MessagingException, UnsupportedEncodingException, FailOTPException, UserNotFoundException;

    User findByEmail(String email);
}