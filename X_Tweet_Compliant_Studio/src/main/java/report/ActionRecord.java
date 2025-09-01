package report;

import java.time.Instant;

public class ActionRecord {
    public String action;
    public String ref;     // tweetId, url, etc.
    public boolean success;
    public String message;
    public long ts = Instant.now().toEpochMilli();

    public ActionRecord() {}
    public ActionRecord(String action, String ref, boolean success, String message) {
        this.action = action;
        this.ref = ref;
        this.success = success;
        this.message = message;
    }
}