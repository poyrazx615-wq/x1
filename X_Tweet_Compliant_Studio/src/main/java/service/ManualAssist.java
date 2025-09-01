package service;

import report.ActionRecord;
import report.Reporter;
import utils.FileUtils;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class ManualAssist {
    private final Consumer<String> log;
    private final Reporter reporter;

    public ManualAssist(Consumer<String> log, Reporter reporter) {
        this.log = log; this.reporter = reporter;
    }

    public void openTweetUrls(String urlsFile, int maxCount) {
        List<String> urls = FileUtils.readLines(urlsFile);
        int count=0;
        for (String url : urls) {
            if (count >= maxCount) break;
            try {
                Desktop.getDesktop().browse(URI.create(url));
                log.accept("🔗 Açıldı: " + url);
                reporter.record(new ActionRecord("open", url, true, "Opened for manual action"));
                count++;
            } catch (Exception e) {
                log.accept("❌ Açılamadı: " + url + " (" + e.getMessage() + ")");
                reporter.record(new ActionRecord("open", url, false, e.getMessage()));
            }
        }
    }
}