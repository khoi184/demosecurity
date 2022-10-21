package com.thuanthanhtech.demosecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    @Size(min = 3, max = 30, message = "Invalid username! (3-30 characters)")
    private String username;
    @Size(min = 6, max = 30, message = "Invalid password! (6-30 characters)")
    private String password;
    private String role;
    private String email;
    private String repeatPassword;
    private String phone;
    private String address;
}
