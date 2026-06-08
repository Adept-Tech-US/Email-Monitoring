package com.company.aiagents.agent;

import com.microsoft.playwright.Page;
import com.company.aiagents.model.PortalCredential;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAgent {
    // takes a browser page + credentials -> fills form -> clicks submit
    //  public void login(Page page, PortalCredential credential) throws Exception 
    //     {
    //         page.fill("#username", credential.getUsername());
    //         page.fill("#password", credential.getPassword());
    //         page.click("button[type='submit']");
            
    //         System.out.println("Login submitted");

    //     }

    public boolean login(Page page, PortalCredential credential) {
        try {
            page.fill("#username", credential.getUsername());
            page.fill("#password", credential.getPassword());
            page.click("button[type='submit']");
            page.waitForLoadState();
            System.out.println("AuthenticationAgent: login submitted for " + credential.getPortalName());
            return true;
        } catch (Exception e) {
            System.err.println("AuthenticationAgent: login failed — " + e.getMessage());
            return false;
        }
    }

            // Implement login logic using Playwright or Selenium
    
    public void submitOtp( // Takes a browser page + OTP -> fills OTP field -> clicks verify
            Page page,
            String otp) {

        page.fill("#otp", otp);
        page.click("#verify");
        page.waitForLoadState();
        System.out.println("AuthenticationAgent: OTP submitted");
    }
        // Return true if authentication is successful, false otherwise
}