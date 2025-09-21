package ifba.orchestrator;

import java.util.Map;

import ifba.common.Task;
import ifba.network.MulticastSync;

public class StateReplicator {
    private final MulticastSync multicast = new MulticastSync();

    public void syncState(Map<String, Task> tasks) {
        // Aqui você pode serializar os dados com JSON por ex.
        String state = "total_tarefas:" + tasks.size(); // exemplo simples
        multicast.send(state);
    }
}