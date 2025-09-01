package api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Twitter API v2 skeleton.
 * Requires user-context OAuth token with write permissions.
 * Fill TW_ACCESS_TOKEN (OAuth 2.0 user token) in config.
 */
public class TwitterApiClient {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20)).build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Properties props;

    public TwitterApiClient(Properties props) {
        this.props = props;
    }

    private String bearer() {
        return props.getProperty("TW_ACCESS_TOKEN","").trim();
    }

    public String postTweet(String text, String inReplyToTweetId, String quoteTweetId) throws Exception {
        if (bearer().isEmpty()) throw new IllegalStateException("TW_ACCESS_TOKEN ayarlanmalı.");
        Map<String,Object> payload = new HashMap<>();
        payload.put("text", text);
        Map<String,Object> reply = new HashMap<>();
        if (inReplyToTweetId != null && !inReplyToTweetId.isBlank()) {
            reply.put("in_reply_to_tweet_id", inReplyToTweetId);
            payload.put("reply", reply);
        }
        if (quoteTweetId != null && !quoteTweetId.isBlank()) {
            payload.put("quote_tweet_id", quoteTweetId);
        }
        String body = mapper.writeValueAsString(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.twitter.com/2/tweets"))
                .header("Authorization", "Bearer " + bearer())
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }
}