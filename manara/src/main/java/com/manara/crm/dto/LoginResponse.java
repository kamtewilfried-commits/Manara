package com.manara.crm.dto;

public class LoginResponse {

    private boolean success;
    private boolean requiresOtp;
    private String message;


    private UserDTO user;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, boolean requiresOtp, String message, UserDTO user) {
        this.success = success;
        this.requiresOtp = requiresOtp;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isRequiresOtp() {
        return requiresOtp;
    }

    public void setRequiresOtp(boolean requiresOtp) {
        this.requiresOtp = requiresOtp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}