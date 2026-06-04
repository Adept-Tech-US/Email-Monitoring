package com.company.aiagents.model;

public class ExtractedData {

    private String fundName;
    private String manager;
    private String amount;
    private String dueDate;
    private String documentType;
    private String referenceNumber;

    // ── Fund Name ──────────────────────────────────────────────────────────
    public String getFundName()       { return fundName; }
    public void setFundName(String v) { this.fundName = v; }

    // ── Manager ────────────────────────────────────────────────────────────
    public String getManager()        { return manager; }
    public void setManager(String v)  { this.manager = v; }

    // ── Amount ─────────────────────────────────────────────────────────────
    public String getAmount()         { return amount; }
    public void setAmount(String v)   { this.amount = v; }

    // ── Due Date ───────────────────────────────────────────────────────────
    public String getDueDate()        { return dueDate; }
    public void setDueDate(String v)  { this.dueDate = v; }

    // ── Document Type ──────────────────────────────────────────────────────
    public String getDocumentType()       { return documentType; }
    public void setDocumentType(String v) { this.documentType = v; }

    // ── Reference Number ───────────────────────────────────────────────────
    public String getReferenceNumber()       { return referenceNumber; }
    public void setReferenceNumber(String v) { this.referenceNumber = v; }

    // ── Amount as double (for Excel numeric cell) ──────────────────────────
    public double getAmountAsDouble() {
        if (amount == null || amount.isBlank()) return 0.0;
        try {
            return Double.parseDouble(
                amount.replace(",", "").replace("$", "").trim()
            );
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}