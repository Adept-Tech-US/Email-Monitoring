// package com.company.aiagents.model;

// public class ExtractedData {

//     private String fundName;
//     private String manager;
//     private String amount;
//     private String dueDate;
//     private String documentType;
//     private String referenceNumber;

//     // ── Fund Name ──────────────────────────────────────────────────────────
//     public String getFundName()       { return fundName; }
//     public void setFundName(String v) { this.fundName = v; }

//     // ── Manager ────────────────────────────────────────────────────────────
//     public String getManager()        { return manager; }
//     public void setManager(String v)  { this.manager = v; }

//     // ── Amount ─────────────────────────────────────────────────────────────
//     public String getAmount()         { return amount; }
//     public void setAmount(String v)   { this.amount = v; }

//     // ── Due Date ───────────────────────────────────────────────────────────
//     public String getDueDate()        { return dueDate; }
//     public void setDueDate(String v)  { this.dueDate = v; }

//     // ── Document Type ──────────────────────────────────────────────────────
//     public String getDocumentType()       { return documentType; }
//     public void setDocumentType(String v) { this.documentType = v; }

//     // ── Reference Number ───────────────────────────────────────────────────
//     public String getReferenceNumber()       { return referenceNumber; }
//     public void setReferenceNumber(String v) { this.referenceNumber = v; }

//     // ── Amount as double (for Excel numeric cell) ──────────────────────────
//     public double getAmountAsDouble() {
//         if (amount == null || amount.isBlank()) return 0.0;
//         try {
//             return Double.parseDouble(
//                 amount.replace(",", "").replace("$", "").trim()
//             );
//         } catch (NumberFormatException e) {
//             return 0.0;
//         }
//     }
// }

package com.company.aiagents.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)  // ignore extra fields AI might return
public class ExtractedData {

    @JsonProperty("fundName")
    private String fundName;

    @JsonProperty("manager")
    private String manager;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    // ── Getters and Setters (same as before) ──────────────────────────────
    public String getFundName()              { return fundName; }
    public void   setFundName(String v)      { this.fundName = v; }
    public String getManager()               { return manager; }
    public void   setManager(String v)       { this.manager = v; }
    public String getAmount()                { return amount; }
    public void   setAmount(String v)        { this.amount = v; }
    public String getDueDate()               { return dueDate; }
    public void   setDueDate(String v)       { this.dueDate = v; }
    public String getDocumentType()          { return documentType; }
    public void   setDocumentType(String v)  { this.documentType = v; }
    public String getReferenceNumber()       { return referenceNumber; }
    public void   setReferenceNumber(String v){ this.referenceNumber = v; }

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