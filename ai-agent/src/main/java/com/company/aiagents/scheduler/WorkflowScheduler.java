package com.company.aiagents.scheduler;

import com.company.aiagents.service.WorkflowService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkflowScheduler {

    private final WorkflowService workflowService;

    public WorkflowScheduler(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // Runs every 5 minutes — adjust as needed
    @Scheduled(fixedDelayString = "${scheduler.delay-ms:300000}")
    public void run() {
        System.out.println("WorkflowScheduler: starting run...");
        try {
            workflowService.run();
        } catch (Exception e) {
            System.err.println("WorkflowScheduler: failed — " + e.getMessage());
        }
    }
}