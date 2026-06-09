package com.company.aiagents.agent;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.company.aiagents.model.PortalCredential;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAgent {

    public boolean login(Page page, PortalCredential credential) {
        try {
            page.fill("#username", credential.getUsername());
            page.fill("#password", credential.getPassword());
            page.click("button[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            System.out.println("AuthenticationAgent: login submitted for " + credential.getPortalName());

            if (credential.getOtpCode() != null && !credential.getOtpCode().isBlank()) {
                boolean otpResult = handleOtpIfNeeded(page, credential);
                if (otpResult) {
                    System.out.println("AuthenticationAgent: OTP authentication succeeded for " + credential.getPortalName());
                } else {
                    System.err.println("AuthenticationAgent: OTP authentication failed for " + credential.getPortalName());
                }
                return otpResult;
            }

            System.out.println("AuthenticationAgent: login completed without OTP for " + credential.getPortalName());
            return true;
        } catch (Exception e) {
            System.err.println("AuthenticationAgent: login failed — " + e.getMessage());
            return false;
        }
    }

    private boolean handleOtpIfNeeded(Page page, PortalCredential credential) {
        try {
            String otpSelector = defaultIfBlank(credential.getOtpSelector(), "#otp");
            String verifySelector = defaultIfBlank(credential.getOtpVerifySelector(), "#verify");

            Locator otpField = page.locator(otpSelector);
            if (!otpField.isVisible()) {
                System.err.println("AuthenticationAgent: OTP code provided but OTP field not visible for " + credential.getPortalName());
                return false;
            }

            page.fill(otpSelector, credential.getOtpCode());
            page.click(verifySelector);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            System.out.println("AuthenticationAgent: OTP submitted successfully for " + credential.getPortalName());
            return true;
        } catch (Exception e) {
            System.err.println("AuthenticationAgent: OTP submission failed — " + e.getMessage());
            return false;
        }
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}