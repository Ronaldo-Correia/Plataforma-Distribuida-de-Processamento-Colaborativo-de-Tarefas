package ifba.worker;

import ifba.common.Task;
import ifba.logs.EventLogger;

public class WorkerNode {
    private final String workerId;

    public WorkerNode(String workerId) {
        this.workerId = workerId;
    }

    public void executeTask(Task task) {
        EventLogger.log("[" + workerId + "] Executando tarefa " + task.getId());
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EventLogger.log("[" + workerId + "] Tarefa " + task.getId() + " concluída.");
    }

    public String getWorkerId() {
        return workerId;
    }
}
