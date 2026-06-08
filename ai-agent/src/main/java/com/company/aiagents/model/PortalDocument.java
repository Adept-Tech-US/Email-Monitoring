package com.company.aiagents.model;

import java.io.File;

public class PortalDocument {

    private String portalName;

    private String documentType;

    private File file;

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(
            String documentType) {
        this.documentType = documentType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}