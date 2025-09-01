package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class Log {
    public static Consumer<String> withTs(Consumer<String> appender) {
        return msg -> {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            appender.accept("[" + ts + "] " + msg);
        };
    }
}