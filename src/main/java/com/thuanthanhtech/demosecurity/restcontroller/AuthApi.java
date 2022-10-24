package com.thuanthanhtech.demosecurity.restcontroller;

import com.thuanthanhtech.demosecurity.config.CustomUserDetails;
import com.thuanthanhtech.demosecurity.dto.LoginRequest;
import com.thuanthanhtech.demosecurity.dto.LoginResponse;
import com.thuanthanhtech.demosecurity.dto.SignUpDto;
import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.exception.UsernameExistedException;
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
@RequestMapping("/api/auth")
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

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) throws UsernameExistedException {
        userService.registerUser(signUpDto);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
