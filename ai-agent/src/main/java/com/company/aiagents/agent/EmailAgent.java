package com.company.aiagents.agent;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class EmailAgent {

    @Value("${email.host}")
    private String host;

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${email.download-dir:downloads}")
    private String downloadDir;

    @Value("${email.port:993}")
    private String port;

    @Value("${email.process-unread-only:true}")
    private boolean processUnreadOnly;

    @Value("${email.mark-processed-read:true}")
    private boolean markProcessedRead;

    // ── Fetch raw Message objects from inbox ──────────────────────────────
    public List<Message> fetchMessages() throws Exception {

        //connect the download folder
        File dir = new File(downloadDir);
        if (!dir.exists()) dir.mkdirs();

        //IMAP connections configuration
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", port);
        props.put("mail.imaps.ssl.enable", "true");

        Session session = Session.getInstance(props);

        // connect to email server 
        Store store     = session.getStore("imaps");
        store.connect(host, username, password);

        // open inbox folder (read-only if not marking processed)
        Folder inbox = store.getFolder("INBOX");
        inbox.open(markProcessedRead ? Folder.READ_WRITE : Folder.READ_ONLY); // allows marking emails as read(READ_WRITE) and only reads emails (READ_ONLY)

        Message[] raw = processUnreadOnly
                ? inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false)) // fetch unread emails, find email where SEEN = false
                : inbox.getMessages();

        // Copy to list so we can close folder safely later
        List<Message> messages = new ArrayList<>(List.of(raw));

        System.out.println("EmailAgent: found " + messages.size() + " message(s).");
        return messages;
    }

    // ── Extract PDF attachments from a single message ─────────────────────
    public List<File> extractPdfs(Message message) throws Exception {

        List<File> pdfs = new ArrayList<>(); // to hold the downloaded PDFs files
        File dir = new File(downloadDir);
        if (!dir.exists()) dir.mkdirs();

        collectPdfs(message, dir, pdfs); // recursively checks every part of the email, since email can contain nested multiparts(Email -> multipart (body + attachments) -> multipart (attachment with text + PDF))

        if (markProcessedRead && !pdfs.isEmpty()) {
            message.setFlag(Flags.Flag.SEEN, true); // if the PDFs were found mark the email as read (SEEN = true)
        }

        return pdfs;
    }

    // ── Extract plain text from email body (no PDF case) ──────────────────
    public String extractBodyText(Message message) throws Exception {
        StringBuilder sb = new StringBuilder(); // Get the email body text when there are no PDF attachments.
        collectBodyText(message, sb); // recursively extracts content.

        if (markProcessedRead) {
            message.setFlag(Flags.Flag.SEEN, true);
        }

        return sb.toString().trim();
    }

    // ── Recursive: collect PDF files from all MIME parts ──────────────────
    private void collectPdfs(Part part, File dir, List<File> pdfs) throws Exception {

        if (part.isMimeType("multipart/*")) { // check if email contains multiple parts (attachments, body, etc.)
            Multipart mp = (Multipart) part.getContent(); // the method loops through all parts
            for (int i = 0; i < mp.getCount(); i++) {
                collectPdfs(mp.getBodyPart(i), dir, pdfs);
            }
            return;
        }

        String fileName = part.getFileName(); // get attachment name
        if (fileName == null) return;

        String decoded = MimeUtility.decodeText(fileName); // Decode special characters
        if (!decoded.toLowerCase().endsWith(".pdf")) return; // check only PDF files are downloaded.
        if (!(part instanceof MimeBodyPart mbp)) return;

        File file = new File(dir, sanitize(decoded));
        if (file.exists()) {
            System.out.println("EmailAgent: skipping existing PDF — " + file.getName());
            return;
        }

        mbp.saveFile(file); // Save PDF to disk
        System.out.println("EmailAgent: downloaded PDF — " + file.getAbsolutePath());
        pdfs.add(file);
    }

    // ── Recursive: collect plain text from email body parts ───────────────
    private void collectBodyText(Part part, StringBuilder sb) throws Exception { // Extract text from email content

        if (part.isMimeType("text/plain")) {
            sb.append(part.getContent().toString()).append("\n");
            return;
        }

        if (part.isMimeType("text/html")) {
            String html = part.getContent().toString();
            sb.append(html.replaceAll("<[^>]+>", " ")).append("\n");
            return;
        }

        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                collectBodyText(mp.getBodyPart(i), sb);
            }
        }
    }

    private String sanitize(String fileName) { // remove invalid file name characters
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_"); 
    }
}