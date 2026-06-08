package com.company.aiagents.scheduler;

import com.company.aiagents.service.WorkflowService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class WorkflowScheduler {

    private final WorkflowService workflowService;

    public WorkflowScheduler(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // Runs every 5 minutes — adjust as needed
    @Scheduled(fixedRateString = "${scheduler.fixed-rate:300000}")
    public void run() {
        System.out.println("WorkflowScheduler: starting run...");
        try {
            workflowService.run();
        } catch (Exception e) {
            System.err.println("WorkflowScheduler: failed — " + e.getMessage());
        }
    }
}
