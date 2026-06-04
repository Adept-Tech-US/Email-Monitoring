package com.company.aiagents.agent;

import com.company.aiagents.model.ExtractedData;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractionAgent {

    // ─────────────────────────────────────────────
    // FUND NAME
    // "Fund: Global Bond Portfolio"
    // "Fund Name: Blackstone Growth Fund IV"
    // "FUND: Apollo Meridian Fund III"
    // ─────────────────────────────────────────────
    private static final Pattern FUND_NAME_PATTERN = Pattern.compile(
        "FUND(?:\\s+NAME)?[:\\s]+([\\w\\s&.,'-]+?)(?=[\\r\\n]|PAYMENT|TOTAL|AMOUNT|MANAGER|$)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    // ─────────────────────────────────────────────
    // MANAGER
    // "Manager: Blackstone Management Partners"
    // "Fund Manager: Apollo Global Management"
    // "Investment Manager: XYZ Capital"
    // ─────────────────────────────────────────────
    private static final Pattern MANAGER_PATTERN = Pattern.compile(
        "(?:FUND\\s+)?(?:INVESTMENT\\s+)?MANAGER[:\\s]+([\\w\\s&.,'-]+?)(?=[\\r\\n]|FUND|PAYMENT|TOTAL|AMOUNT|$)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    // ─────────────────────────────────────────────
    // PAYMENT DUE DATE
    // "PAYMENT DUE: 2024-01-31"
    // "Due Date: 15 June 2026"
    // "Payment Due Date: 20 June 2026"
    // "Distribution Date: 30 May 2026"
    // ─────────────────────────────────────────────
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "(?:PAYMENT\\s+DUE(?:\\s+DATE)?|DUE\\s+DATE|DISTRIBUTION\\s+DATE)" +
        "[:\\s]+([\\d]{1,2}[\\s][A-Za-z]+[\\s][\\d]{4}" +   // 15 June 2026
        "|[\\d]{1,2}[/-][\\d]{1,2}[/-][\\d]{2,4}" +          // 15/06/2026 or 15-06-2026
        "|[\\d]{4}-[\\d]{2}-[\\d]{2})",                       // 2026-06-15
        Pattern.CASE_INSENSITIVE
    );

    // ─────────────────────────────────────────────
    // TOTAL AMOUNT
    // "TOTAL: $12,500.00"
    // "Call Amount: $250,000"
    // "Total This Call: $392,500"
    // "Net Distribution: $15,520,000"
    // "Amount Due: $250,000"
    // ─────────────────────────────────────────────
    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
        "(?:TOTAL(?:\\s+THIS\\s+CALL)?|AMOUNT\\s+DUE|CALL\\s+AMOUNT|NET\\s+DISTRIBUTION)" +
        "[:\\s]+\\$?([\\d,]+(?:\\.[\\d]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    );

    // ─────────────────────────────────────────────
    // DOCUMENT TYPE
    // "Document Type: Capital Call Notice"
    // "Type: Distribution Notice"
    // "Notice Type: Quarterly Statement"
    // ─────────────────────────────────────────────
    private static final Pattern DOC_TYPE_PATTERN = Pattern.compile(
        "(?:DOCUMENT\\s+TYPE|NOTICE\\s+TYPE|TYPE)[:\\s]+([\\w\\s&.,'-]+?)(?=[\\r\\n]|FUND|PAYMENT|REF|$)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    // ─────────────────────────────────────────────
    // REFERENCE NUMBER
    // "Reference: TXN-2024-00123"
    // "Ref No: 456789"
    // "Reference Number: CC-2026-001"
    // ─────────────────────────────────────────────
    private static final Pattern REF_PATTERN = Pattern.compile(
        "(?:REFERENCE(?:\\s+NUMBER)?|REF(?:\\s+NO|\\.)?)[:\\s]+([\\w\\-/]+)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Parses raw text (from PDF or email body) and extracts
     * fund name, manager, due date, amount, document type, and reference number.
     */
    public ExtractedData extract(String text) {

        if (text == null || text.isBlank()) {
            System.err.println("ExtractionAgent: empty text received.");
            return new ExtractedData();
        }

        // Normalize line endings — PDFs often use \r\n or \r alone
        String t = text.replace("\r\n", "\n").replace("\r", "\n");

        ExtractedData data = new ExtractedData();

        // ── Fund Name ──────────────────────────────────────────────────────
        Matcher m = FUND_NAME_PATTERN.matcher(t);
        if (m.find()) {
            data.setFundName(m.group(1).trim());
            System.out.println("ExtractionAgent: fundName        = " + data.getFundName());
        } else {
            System.err.println("ExtractionAgent: fund name not found.");
        }

        // ── Manager ────────────────────────────────────────────────────────
        m = MANAGER_PATTERN.matcher(t);
        if (m.find()) {
            data.setManager(m.group(1).trim());
            System.out.println("ExtractionAgent: manager         = " + data.getManager());
        } else {
            System.err.println("ExtractionAgent: manager not found.");
        }

        // ── Payment Due Date ───────────────────────────────────────────────
        m = DATE_PATTERN.matcher(t);
        if (m.find()) {
            data.setDueDate(m.group(1).trim());
            System.out.println("ExtractionAgent: dueDate         = " + data.getDueDate());
        } else {
            System.err.println("ExtractionAgent: due date not found.");
        }

        // ── Total Amount ───────────────────────────────────────────────────
        m = AMOUNT_PATTERN.matcher(t);
        if (m.find()) {
            data.setAmount(m.group(1).replace(",", ""));
            System.out.println("ExtractionAgent: amount          = " + data.getAmount());
        } else {
            System.err.println("ExtractionAgent: amount not found.");
        }

        // ── Document Type ──────────────────────────────────────────────────
        m = DOC_TYPE_PATTERN.matcher(t);
        if (m.find()) {
            data.setDocumentType(m.group(1).trim());
            System.out.println("ExtractionAgent: documentType    = " + data.getDocumentType());
        } else {
            System.err.println("ExtractionAgent: document type not found.");
        }

        // ── Reference Number ───────────────────────────────────────────────
        m = REF_PATTERN.matcher(t);
        if (m.find()) {
            data.setReferenceNumber(m.group(1).trim());
            System.out.println("ExtractionAgent: referenceNumber = " + data.getReferenceNumber());
        } else {
            System.err.println("ExtractionAgent: reference number not found.");
        }

        return data;
    }
}