package com.company.aiagents.agent;

import com.company.aiagents.model.PortalCredential;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationAgentTest {

    @Test
    void loginWithoutOtpCompletesSuccessfully() {
        Page page = mock(Page.class);
        PortalCredential cred = new PortalCredential();
        cred.setPortalName("test-portal");
        cred.setUsername("user");
        cred.setPassword("pass");
        cred.setOtpCode(null);

        AuthenticationAgent agent = new AuthenticationAgent();

        boolean result = agent.login(page, cred);

        assertTrue(result);
        verify(page).fill("#username", "user");
        verify(page).fill("#password", "pass");
        verify(page).click("button[type='submit']");
    }

    @Test
    void loginWithOtpButFieldNotVisibleFails() {
        Page page = mock(Page.class);
        Locator locator = mock(Locator.class);
        when(page.locator("#otp")).thenReturn(locator);
        when(locator.isVisible()).thenReturn(false);

        PortalCredential cred = new PortalCredential();
        cred.setPortalName("test-portal");
        cred.setUsername("user");
        cred.setPassword("pass");
        cred.setOtpCode("123456");
        cred.setOtpSelector("#otp");
        cred.setOtpVerifySelector("#verify");

        AuthenticationAgent agent = new AuthenticationAgent();

        boolean result = agent.login(page, cred);

        assertFalse(result);
        verify(page).fill("#username", "user");
        verify(page).fill("#password", "pass");
        verify(page).click("button[type='submit']");
        verify(locator).isVisible();
    }

    @Test
    void loginWithOtpAndFieldVisibleSubmitsOtp() {
        Page page = mock(Page.class);
        Locator locator = mock(Locator.class);
        when(page.locator("#otp")).thenReturn(locator);
        when(locator.isVisible()).thenReturn(true);

        PortalCredential cred = new PortalCredential();
        cred.setPortalName("test-portal");
        cred.setUsername("user");
        cred.setPassword("pass");
        cred.setOtpCode("123456");
        cred.setOtpSelector("#otp");
        cred.setOtpVerifySelector("#verify");

        AuthenticationAgent agent = new AuthenticationAgent();

        boolean result = agent.login(page, cred);

        assertTrue(result);
        verify(page).fill("#username", "user");
        verify(page).fill("#password", "pass");
        verify(page).click("button[type='submit']");
        verify(locator).isVisible();
        verify(page).fill("#otp", "123456");
        verify(page).click("#verify");
    }
}
