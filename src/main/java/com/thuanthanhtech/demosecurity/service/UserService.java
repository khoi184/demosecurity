package com.thuanthanhtech.demosecurity.service;

import com.thuanthanhtech.demosecurity.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUserId(Long id);

    User findByUsername(String username);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    void changeAccountPassword(User user, String password);

    String forgotPassword(String email);

    String resetPassword(String token, String password);

    User findByToken(String token);

    void updateResetPasswordToken(String token, String email) throws Exception;
}