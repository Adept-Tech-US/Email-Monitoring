package com.company.aiagents.agent;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DownloadAgentTest {

    @Test
    void downloadFiles_savesPdfDownloadsToConfiguredDirectory() throws Exception {
        Page page = mock(Page.class);
        Locator locator = mock(Locator.class);
        Locator pdfLocator = mock(Locator.class);
        Download download = mock(Download.class);

        when(page.locator("a[href$='.pdf']")).thenReturn(locator);
        when(locator.count()).thenReturn(1);
        when(locator.nth(0)).thenReturn(pdfLocator);
        when(page.waitForDownload(any())).thenAnswer(invocation -> {
            Runnable action = invocation.getArgument(0);
            action.run();
            return download;
        });
        when(download.suggestedFilename()).thenReturn("document.pdf");

        Path tempDir = Files.createTempDirectory("downloadagent-test");
        File downloadDir = tempDir.toFile();

        doAnswer(invocation -> {
            Path target = invocation.getArgument(0);
            Files.createDirectories(target.getParent());
            Files.writeString(target, "dummy content");
            return null;
        }).when(download).saveAs(any(Path.class));

        DownloadAgent agent = new DownloadAgent();
        Field field = DownloadAgent.class.getDeclaredField("downloadDir");
        field.setAccessible(true);
        field.set(agent, downloadDir.getAbsolutePath());

        List<File> files = agent.downloadFiles(page);

        assertEquals(1, files.size());
        File saved = files.get(0);
        assertTrue(saved.exists());
        assertEquals(downloadDir.getAbsolutePath(), saved.getParent());
        assertEquals("document.pdf", saved.getName());
        assertEquals("dummy content", Files.readString(saved.toPath()));
        verify(pdfLocator).click();
    }
}
