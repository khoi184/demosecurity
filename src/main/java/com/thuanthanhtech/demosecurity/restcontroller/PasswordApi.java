package com.thuanthanhtech.demosecurity.restcontroller;

import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordApi {

    private final UserService userService;

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
    public String forgotPassword(@RequestParam("email") String email) {

        String response = userService.forgotPassword(email);

        if (!response.startsWith("Invalid")) {
            response = "http://localhost:8080/reset-password?token=" + response;
        }
        return response;
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password) {

        return userService.resetPassword(token, password);
    }
}
