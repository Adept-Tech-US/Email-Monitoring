package com.company.aiagents.agent;

import com.microsoft.playwright.*;
import com.company.aiagents.model.PortalCredential;
import org.springframework.stereotype.Component;

// @Component
// public class PortalAgent {

//     // public List<File> downloadDocuments(String portalUrl, PortalCredentials credentials) throws Exception {
//     // plawright / Selenium Login

//     // Navigate documents page

//     // Download PDFs

//     private final AuthenticationAgent authAgent =
//             new AuthenticationAgent();

//     public Page connect(
//             String portalUrl,
//             PortalCredential credential)
//             throws Exception {

//         Playwright playwright =
//                 Playwright.create();

//         Browser browser =
//                 playwright.chromium().launch(
//                         new BrowserType.LaunchOptions()
//                                 .setHeadless(false));

//         Page page =
//                 browser.newPage();

//         page.navigate(portalUrl);

//         authAgent.login(
//                 page,
//                 credential);

//         return page;
//     }
    
    
//     // return downloadedFiles;
//     // return Arrays.asList("Portal A", "Portal B", "Portal C");

//     // Open Portal -> Login -> Navigate to document menus -> Download files

// }


@Component
public class PortalAgent {

    private final AuthenticationAgent authAgent;

    public PortalAgent(AuthenticationAgent authAgent) {
        this.authAgent = authAgent;
    }

    /**
     * Opens a browser, navigates to the portal, logs in, and returns
     * both the Page and the Playwright instance so the caller can close them.
     */
    public PlaywrightSession connect(String portalUrl, PortalCredential credential) throws Exception {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)); // headless=true for prod
        Page page = browser.newPage();
        page.navigate(portalUrl);
        boolean ok = authAgent.login(page, credential);
        if (!ok) {
            browser.close();
            playwright.close();
            throw new RuntimeException("PortalAgent: login failed for " + portalUrl);
        }
        return new PlaywrightSession(playwright, browser, page);
    }

    /** Wrapper so callers can try-with-resources or close() explicitly. */
    public record PlaywrightSession(Playwright playwright, Browser browser, Page page)
            implements AutoCloseable {
        @Override
        public void close() {
            browser.close();
            playwright.close();
        }
    }
}