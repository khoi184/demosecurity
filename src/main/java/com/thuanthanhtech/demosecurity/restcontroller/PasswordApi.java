package com.thuanthanhtech.demosecurity.restcontroller;

import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.exception.FailOTPException;
import com.thuanthanhtech.demosecurity.exception.UserNotFoundException;
import com.thuanthanhtech.demosecurity.service.EmailService;
import com.thuanthanhtech.demosecurity.service.UserService;
import com.thuanthanhtech.demosecurity.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordApi {

    private final UserService userService;

    private final EmailService emailService;

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@RequestParam("old-password") String oldPassword,
                                                 @RequestParam("new-password") String newPassword,
                                                 @RequestParam("username") String username) throws Exception {
        User user = userService.findByUsername(username);

        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            throw new Exception("Invalid Old Password!");
        }

        if (user.getUsername().equals(username)) {
            userService.changeAccountPassword(user, newPassword);
            return new ResponseEntity<>("Change password successful!", HttpStatus.OK);
        } else {
            throw new UserNotFoundException(username);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email, HttpServletRequest request) {

        String token = userService.forgotPassword(email);
        try {

            if (!token.startsWith("Invalid")) {
                token = Utility.getSiteURL(request) + "/reset-password?token=" + token;
                emailService.sendEmail(email, token);
            }
        } catch (UsernameNotFoundException e) {
            e.getMessage();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Email has been sent with token: " + token, HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("password") String password) {

        return new ResponseEntity<>(userService.resetPassword(token, password), HttpStatus.OK);
    }

    @PostMapping("/reset-default-password")
    public ResponseEntity<String> resetDefaultPassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {

        User user = userService.findByEmail(email);
        Integer OTP = emailService.generateOneTimePassword(user);
        try {
            emailService.sendOtpEmail(userService.findByEmail(email), OTP);
        } catch (UsernameNotFoundException e) {
            e.getMessage();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Email has been sent with OTP: " + OTP, HttpStatus.OK);
    }

    @PutMapping("reset-default")
    public ResponseEntity<String> resetToDefault(@RequestParam("email") String email,
                                                 @RequestParam("otp") Integer otp) throws FailOTPException, UserNotFoundException {
        try {
            userService.validateOTP(otp, email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Password was reset to default: 123456", HttpStatus.OK);
    }
}
