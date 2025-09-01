package manage;

import api.TwitterApiClient;
import report.Reporter;
import service.ManualAssist;
import utils.Log;

import javax.swing.*;
import java.util.Properties;
import java.util.function.Consumer;

public class Controller {
    private final Consumer<String> log;
    private final Reporter reporter = new Reporter();
    private final ScheduleManager scheduleManager = new ScheduleManager(Log.withTs(msg -> log.accept(msg)), reporter);

    public Controller(Consumer<String> log) {
        this.log = log;
    }

    public Reporter getReporter() { return reporter; }

    public void composeNow(Properties props, String text, String replyTo, String quoteId) {
        new SwingWorker<Void, String>(){
            @Override protected Void doInBackground() {
                try {
                    TwitterApiClient api = new TwitterApiClient(props);
                    String result = api.postTweet(text, replyTo, quoteId);
                    log.accept("✅ Tweet gönderildi. API cevabı: " + result);
                } catch (Exception e) {
                    log.accept("❌ Gönderim hatası: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    public void scheduleCompose(Properties props, String text, String replyTo, String quoteId, String cron) {
        try {
            scheduleManager.scheduleCompose(props, text, replyTo, quoteId, cron);
        } catch (Exception e) {
            log.accept("❌ Planlama hatası: " + e.getMessage());
        }
    }

    public void stopAllSchedules() {
        try { scheduleManager.stopAll(); } catch (Exception e) { log.accept("❌ " + e.getMessage()); }
    }

    public void openManualUrls(String urlsFile, int maxCount) {
        new ManualAssist(log, reporter).openTweetUrls(urlsFile, maxCount);
    }
}