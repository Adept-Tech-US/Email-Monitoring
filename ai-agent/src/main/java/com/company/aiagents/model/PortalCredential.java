package com.company.aiagents.model;

public class PortalCredential {
    private String portalName;
    private String username;
    private String password;
    private String otpType;
    private String otpCode;
    private String otpSelector;
    private String otpVerifySelector;

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtpType() {
        return otpType;
    }

    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpSelector() {
        return otpSelector;
    }

    public void setOtpSelector(String otpSelector) {
        this.otpSelector = otpSelector;
    }

    public String getOtpVerifySelector() {
        return otpVerifySelector;
    }

    public void setOtpVerifySelector(String otpVerifySelector) {
        this.otpVerifySelector = otpVerifySelector;
    }
}