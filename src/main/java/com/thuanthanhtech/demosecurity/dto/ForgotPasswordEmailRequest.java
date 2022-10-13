package com.thuanthanhtech.demosecurity.dto;

import lombok.Data;

@Data
public class ForgotPasswordEmailRequest {

    private String forgotPasswordEmail;

    private String newPassword;

}
