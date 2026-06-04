package com.example.ai_agent;

import com.company.aiagents.Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class, properties = "scheduler.enabled=false")
class AiAgentApplicationTests {

	@Test
	void contextLoads() {
	}

}
