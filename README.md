# Email Monitoring System using AI Agents (Java)

## Overview

This project automates the process of reading emails from Gmail or Outlook inboxes, downloading PDF attachments, extracting data from the PDFs, and updating an Excel sheet with the extracted information.
The solution is implemented using **Java** and follows an **AI Agent-based architecture** to separate responsibilities and improve maintainability.

---
## Overview
https://docs.google.com/document/d/1me3yiLcfNvh_l8vw6bZ2jAJkzmqniPhfDuvji0-7y3s/edit?usp=sharing


## Exiting Objectives

* Monitor Gmail or Outlook inboxes.
* Detect emails containing PDF attachments.
* Download and process PDF files.
* Extract text content from PDFs.
* Identify and extract relevant numerical data and fields.
* Update an Excel spreadsheet automatically with the extracted data.

---

## AI Agent Architecture

### 1. Email Agent

**Responsibilities:**
* Connect to Gmail or Outlook mailbox.
* Read incoming emails.
* Detect PDF attachments.
* Download PDF files for processing.

### 2. PDF Processing Agent
**Responsibilities:**
* Receive downloaded PDF files.
* Extract text content from PDF documents.
* Handle both text-based and scanned PDFs (OCR support can be added).

### 3. Data Extraction Agent
**Responsibilities:**
* Analyze extracted text.
* Identify required fields and numerical values.
* Structure the extracted data into a predefined format.

### 4. Excel Agent
**Responsibilities:**
* Open or create Excel files.
* Insert extracted data into appropriate columns.
* Save and update the Excel workbook.

---

## Workflow

```text
Gmail / Outlook Inbox
          ↓
      Email Agent
          ↓
     Download PDF
          ↓
 PDF Processing Agent
          ↓
    Extract PDF Text
          ↓
  Data Extraction Agent
          ↓
 Extract Numbers / Fields
          ↓
      Excel Agent
          ↓
   Update Excel Sheet
```
## Project Structure

```text
src/
├── email-agent/
│   └── EmailAgent.java
│
├── pdf-agent/
│   └── PdfProcessingAgent.java
│
├── extraction-agent/
│   └── DataExtractionAgent.java
│
├── excel-agent/
│   └── ExcelAgent.java
│
└── main/
    └── Application.java
```

## Future Enhancements

* Portal Access Automation

---
