package com.company.aiagents.repository;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ProcessedDocumentRepository {

    // private final Set<String> documents =
    //         new HashSet<>();

    // public boolean alreadyProcessed(
    //         String name) {

    //     return documents.contains(name);
    // }

    // public void save(
    //         String name) {

    //     documents.add(name);
    // }

    private final Set<String> processed = Collections.synchronizedSet(new HashSet<>());

    public boolean alreadyProcessed(String name) {
        return processed.contains(name);
    }

    public void markProcessed(String name) {
        processed.add(name);
        System.out.println("ProcessedDocumentRepository: marked — " + name);
    }

}