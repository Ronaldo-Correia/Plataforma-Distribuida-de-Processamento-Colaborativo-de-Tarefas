package ifba.orchestrator;

import java.util.stream.Collectors;

import ifba.common.HeartbeatMonitor;
import ifba.common.LamportClock;
import ifba.common.Task;
import ifba.logs.EventLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainOrchestrator {
    private final LoadBalancer balancer;
    private final Map<String, Task> runningTasks = new ConcurrentHashMap<>();
    private final LamportClock clock = new LamportClock();
    private final StateReplicator replicator;
    private final HeartbeatMonitor heartbeatMonitor;

    public MainOrchestrator() {
    this.balancer = new LoadBalancer(LoadBalancer.Policy.ROUND_ROBIN);
    this.replicator = new StateReplicator();
    this.heartbeatMonitor = new HeartbeatMonitor(this); 
}

    public void receiveTask(Task task) {
        clock.increment();
        task.setTimestamp(clock.getTime());

        String workerId = balancer.selectWorker();
        runningTasks.put(task.getId(), task);
        
        // Log e checkpoint
        EventLogger.log("Tarefa recebida: " + task.getId());
        replicator.syncState(runningTasks);

        sendToWorker(workerId, task);
    }

    public void handleWorkerFailure(String workerId) {
        List<Task> failedTasks = getTasksByWorker(workerId);

        for (Task task : failedTasks) {
            String newWorker = balancer.selectWorker();
            EventLogger.log("Redistribuindo tarefa " + task.getId() + " para " + newWorker);
            sendToWorker(newWorker, task);
        }

        replicator.syncState(runningTasks);
    }

    private void sendToWorker(String workerId, Task task) {
    EventLogger.log("Enviando tarefa " + task.getId() + " para " + workerId + " | Lamport: " + clock.getTime());
    }


    private List<Task> getTasksByWorker(String workerId) {
        return runningTasks.values().stream()
                .filter(t -> t.getAssignedWorker().equals(workerId))
                .collect(Collectors.toList());
    }
    public void recoverFromBackup(Map<String, Task> state) {
    runningTasks.clear();
    runningTasks.putAll(state);
    EventLogger.log("Recuperado estado do backup: " + state.size() + " tarefas.");
}
public HeartbeatMonitor getHeartbeatMonitor() {
    return heartbeatMonitor;
}

}
