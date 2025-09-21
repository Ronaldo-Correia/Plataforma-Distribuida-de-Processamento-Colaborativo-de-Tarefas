package ifba.orchestrator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import ifba.common.Task;
import ifba.logs.EventLogger;
import ifba.network.MulticastSync;
import ifba.orchestrator.MainOrchestrator;

public class BackupOrchestrator {
    private Map<String, Task> replicatedTasks = new ConcurrentHashMap<>();
    private boolean isPrimary = false;

    public void startListening() {
    ObjectMapper mapper = new ObjectMapper();

    new MulticastSync().startListening((json) -> {
        try {
            Map<String, Task> received = mapper.readValue(json,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Task.class));
            this.replicatedTasks = received;
            EventLogger.log("Estado sincronizado com " + received.size() + " tarefas.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}

    public void becomePrimary() {
        isPrimary = true;
        new MainOrchestrator().recoverFromBackup(replicatedTasks);
        EventLogger.log("Backup assumiu como principal");
    }
}
