package com.manara.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OtpLoginRequest {

    @NotNull(message = "userId est obligatoire")
    private Long userId;

    @NotBlank(message = "Le code OTP est obligatoire")
    private String otp;

    public OtpLoginRequest() {
    }

    public OtpLoginRequest(Long userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}