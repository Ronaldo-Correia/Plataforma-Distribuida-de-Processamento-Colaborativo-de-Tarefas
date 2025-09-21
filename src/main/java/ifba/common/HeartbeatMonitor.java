package ifba.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ifba.logs.EventLogger;
import ifba.orchestrator.MainOrchestrator;

public class HeartbeatMonitor {
    private final Map<String, Long> lastSeen = new ConcurrentHashMap<>();
    private final long timeout = 5000;
    private final MainOrchestrator orchestrator;

    public HeartbeatMonitor(MainOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void receiveHeartbeat(String workerId) {
        lastSeen.put(workerId, System.currentTimeMillis());
    }

    public void checkFailures() {
        long now = System.currentTimeMillis();
        for (String worker : lastSeen.keySet()) {
            if (now - lastSeen.get(worker) > timeout) {
                EventLogger.log("Falha detectada no worker: " + worker);
                orchestrator.handleWorkerFailure(worker);
            }
        }
    }
}
