package com.company.aiagents.agent;

import com.microsoft.playwright.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class DownloadAgent {

    @Value("${email.download-dir:downloads}")
    private String downloadDir;

//     public List<File> downloadFiles(
//             Page page)
//             throws Exception {

//         List<File> files =
//                 new ArrayList<>();

//         Locator pdfLinks =
//                 page.locator(
//                         "a[href$='.pdf']");

//         int count =
//                 pdfLinks.count();

//         for (int i = 0; i < count; i++) {
//             final int index = i;

//             Download download =
//                     page.waitForDownload(
//                             () -> pdfLinks.nth(index).click());

//             String fileName =
//                     download.suggestedFilename();

//             File file =
//                     new File(
//                             "downloads/" + fileName);

//             download.saveAs(
//                     Paths.get(
//                             file.getAbsolutePath()));

//             files.add(file);
//         }

//         return files;
//     }

        public List<File> downloadFiles(Page page) throws Exception {
        List<File> files = new ArrayList<>();
        File dir = new File(downloadDir);
        if (!dir.exists()) dir.mkdirs();

        Locator pdfLinks = page.locator("a[href$='.pdf']");
        int count = pdfLinks.count();

        for (int i = 0; i < count; i++) {
            final int index = i;
            Download download = page.waitForDownload(() -> pdfLinks.nth(index).click());
            String fileName = download.suggestedFilename();
            File file = new File(dir, fileName);
            download.saveAs(Paths.get(file.getAbsolutePath()));
            files.add(file);
            System.out.println("DownloadAgent: saved — " + file.getAbsolutePath());
        }
        return files;
    }
}