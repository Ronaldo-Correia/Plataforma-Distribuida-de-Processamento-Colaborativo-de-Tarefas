package ifba.orchestrator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    public enum Policy { ROUND_ROBIN }

    private final Policy policy;
    private final List<String> workers = List.of("worker1", "worker2", "worker3");
    private final AtomicInteger index = new AtomicInteger(0);

    public LoadBalancer(Policy policy) {
        this.policy = policy;
    }

    public String selectWorker() {
        if (policy == Policy.ROUND_ROBIN) {
            int i = index.getAndUpdate(n -> (n + 1) % workers.size());
            return workers.get(i);
        }
        return workers.get(0); 
    }
}