package com.thuanthanhtech.demosecurity.restcontroller;

import com.thuanthanhtech.demosecurity.config.CustomUserDetails;
import com.thuanthanhtech.demosecurity.dto.LoginRequest;
import com.thuanthanhtech.demosecurity.dto.LoginResponse;
import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.jwt.JwtTokenUtil;
import com.thuanthanhtech.demosecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthApi {

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenUtil.generateToken((CustomUserDetails) authentication.getPrincipal());
        return new LoginResponse(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Logout successfully!", HttpStatus.OK);
    }

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
}
