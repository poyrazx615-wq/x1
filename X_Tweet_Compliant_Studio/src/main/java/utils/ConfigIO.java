package utils;

import java.io.*;
import java.util.Properties;

public class ConfigIO {
    public static Properties load(String path) {
        Properties p = new Properties();
        File f = new File(path);
        if (f.exists()) {
            try (FileInputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (IOException ignored) {}
        }
        return p;
    }

    public static void save(Properties p, String path) throws IOException {
        try (FileOutputStream out = new FileOutputStream(path)) {
            p.store(out, "XTweet Config");
        }
    }
}