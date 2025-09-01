package report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Reporter {
    private final List<ActionRecord> buffer = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public synchronized void record(ActionRecord rec) {
        buffer.add(rec);
    }

    public synchronized List<ActionRecord> getAll() {
        return new ArrayList<>(buffer);
    }

    public synchronized void flushCSV(String path) {
        try {
            var sb = new StringBuilder();
            sb.append("ts,action,ref,success,message\n");
            for (var r : buffer) {
                String msg = r.message==null? "" : r.message.replaceAll("[\r\n,]"," ");
                sb.append(r.ts).append(',').append(r.action).append(',').append(r.ref)
                  .append(',').append(r.success).append(',').append(msg).append('\n');
            }
            Files.writeString(Path.of(path), sb.toString());
        } catch (IOException ignored) {}
    }

    public synchronized void flushJSON(String path) {
        try {
            Files.writeString(Path.of(path), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(buffer));
        } catch (IOException ignored) {}
    }
}