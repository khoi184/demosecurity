package com.thuanthanhtech.demosecurity.restcontroller;

import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.service.EmailService;
import com.thuanthanhtech.demosecurity.service.UserService;
import com.thuanthanhtech.demosecurity.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
                                                 @RequestParam("new-password") String newPassword) throws Exception {
        User user = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            throw new Exception("Invalid Old Password!");
        }
        userService.changeAccountPassword(user, newPassword);
        return new ResponseEntity<String>("Change password successful!", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, HttpServletRequest request) {

        String token = userService.forgotPassword(email);
        try {

            if (!token.startsWith("Invalid")) {
                token = Utility.getSiteURL(request) + "/reset-password?token=" + token;
                emailService.sendEmail(email, token);
            }
        } catch (UsernameNotFoundException e) {
            e.getMessage();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Email has been sent!";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password) {

        return userService.resetPassword(token, password);
    }
}
