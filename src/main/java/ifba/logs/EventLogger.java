package ifba.logs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EventLogger {
    public static void log(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[LOG - " + timestamp + "] " + message);
    }
}