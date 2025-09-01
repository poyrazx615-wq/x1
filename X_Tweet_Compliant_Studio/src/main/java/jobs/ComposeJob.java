package jobs;

import api.TwitterApiClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import report.Reporter;

import java.util.Properties;
import java.util.function.Consumer;

public class ComposeJob implements Job {
    public static Consumer<String> LOGGER;
    public static Properties PROPS;
    public static Reporter REPORTER;
    public static String TEXT;
    public static String REPLY_TO;
    public static String QUOTE_ID;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (LOGGER == null || PROPS == null || TEXT == null) return;
        try {
            TwitterApiClient api = new TwitterApiClient(PROPS);
            String result = api.postTweet(TEXT, REPLY_TO, QUOTE_ID);
            LOGGER.accept("✅ Tweet gönderildi (planlı). API cevabı: " + result);
        } catch (Exception e) {
            LOGGER.accept("❌ Planlı gönderim hatası: " + e.getMessage());
        }
    }
}