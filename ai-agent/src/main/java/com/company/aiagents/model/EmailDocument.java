// package com.company.aiagents.model;

// public class EmailDocument {

//     private String sender;
//     private String subject;
//     private String body;
//     private String attachmentPath;
//     private String receivedDate;

//     // Getters and Setters

//     public String getSender() {
//         return sender;
//     }

//     public void setSender(String sender) {
//         this.sender = sender;
//     }

//     public String getSubject() {
//         return subject;
//     }

//     public void setSubject(String subject) {
//         this.subject = subject;
//     }

//     public String getBody() {
//         return body;
//     }

//     public void setBody(String body) {
//         this.body = body;
//     }

//     public String getAttachmentPath() {
//         return attachmentPath;
//     }

//     public void setAttachmentPath(String attachmentPath) {
//         this.attachmentPath = attachmentPath;
//     }

//     public String getReceivedDate() {
//         return receivedDate;
//     }

//     public void setReceivedDate(String receivedDate) {
//         this.receivedDate = receivedDate;
//     }
// }


package com.company.aiagents.model;

public class EmailDocument {
    private String sender;
    private String subject;
    private String body;
    private String attachmentPath; // local or S3 path after download
    private String receivedDate;

    public String getSender()               { return sender; }
    public void   setSender(String v)       { this.sender = v; }
    public String getSubject()              { return subject; }
    public void   setSubject(String v)      { this.subject = v; }
    public String getBody()                 { return body; }
    public void   setBody(String v)         { this.body = v; }
    public String getAttachmentPath()       { return attachmentPath; }
    public void   setAttachmentPath(String v){ this.attachmentPath = v; }
    public String getReceivedDate()         { return receivedDate; }
    public void   setReceivedDate(String v) { this.receivedDate = v; }
}