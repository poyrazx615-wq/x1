package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<String> readLines(String path) {
        try {
            return Files.lines(Path.of(path))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }
}