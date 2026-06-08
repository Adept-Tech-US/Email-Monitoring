package com.company.aiagents.service;

import com.company.aiagents.model.PortalCredential;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "portal.url=https://ubiquitous-crumble-a05f3a.netlify.app/")
@Disabled("Integration test that requires external portal access")
class PortalWorkflowServiceIntegrationTest {

    @Autowired
    private PortalWorkflowService service;

    @Test
    void testPortalWorkflow() throws Exception {
        PortalCredential credential = new PortalCredential();
        credential.setUsername("lp@testfund.com");
        credential.setPassword("password123");
        credential.setPortalName("test-portal");

        service.run(credential);
    }
}