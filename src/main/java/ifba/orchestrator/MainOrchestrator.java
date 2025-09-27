package ifba.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifba.common.HeartbeatMonitor;
import ifba.common.LamportClock;
import ifba.common.Task;
import ifba.logs.EventLogger;
import ifba.network.AuthManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainOrchestrator {
    private final LoadBalancer balancer;
    private final Map<String, Task> runningTasks = new ConcurrentHashMap<>();
    private final LamportClock clock = new LamportClock();
    private final StateReplicator replicator;
    private final HeartbeatMonitor heartbeatMonitor;
    private final AuthManager auth = new AuthManager(); // Compartilhado

    public MainOrchestrator() {
        this.balancer = new LoadBalancer(LoadBalancer.Policy.ROUND_ROBIN);
        this.replicator = new StateReplicator();
        this.heartbeatMonitor = new HeartbeatMonitor(this);
    }

    public void receiveTask(Task task) {
        clock.increment();
        task.setTimestamp(clock.getTime());

        String workerId = balancer.selectWorker();
        task.setAssignedWorker(workerId);
        task.setStatus(Task.Status.RUNNING);
        runningTasks.put(task.getId(), task);

        EventLogger.log("Tarefa recebida: " + task.getId());
        replicator.syncState(runningTasks);

        sendToWorker(workerId, task);
    }

    public void handleWorkerFailure(String workerId) {
        List<Task> failedTasks = getTasksByWorker(workerId);

        for (Task task : failedTasks) {
            task.setStatus(Task.Status.FAILED);
            String newWorker = balancer.selectWorker();
            task.setAssignedWorker(newWorker);
            task.setStatus(Task.Status.RUNNING);
            EventLogger.log("Redistribuindo tarefa " + task.getId() + " para " + newWorker);
            sendToWorker(newWorker, task);
        }

        replicator.syncState(runningTasks);
    }

    private void sendToWorker(String workerId, Task task) {
        try (Socket socket = new Socket("localhost", getWorkerPort(workerId));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(task);
            out.println(json);

            EventLogger.log("Tarefa " + task.getId() + " enviada para " + workerId);
        } catch (IOException e) {
            EventLogger.log("Erro ao enviar tarefa para " + workerId + ": " + e.getMessage());
        }
    }

    private int getWorkerPort(String workerId) {
        return switch (workerId) {
            case "worker1" -> 6001;
            case "worker2" -> 6002;
            case "worker3" -> 6003;
            default -> 6000;
        };
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

    public void startServer(int port) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                EventLogger.log("Orquestrador escutando na porta " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String message = in.readLine();

                    if (message == null) continue;

                    if (message.startsWith("HEARTBEAT:")) {
                        String workerId = message.split(":")[1];
                        heartbeatMonitor.receiveHeartbeat(workerId);
                        EventLogger.log("Heartbeat recebido de " + workerId);

                    } else if (message.startsWith("STATUS:")) {
                        String json = message.substring(7);
                        handleStatusUpdate(json);

                    } else if (message.startsWith("TASK:")) {
                        String[] parts = message.split(":", 4);
                        String username = parts[1];
                        String password = parts[2];
                        String json = parts[3];

                        if (auth.login(username, password) != null) {
                            Task task = new ObjectMapper().readValue(json, Task.class);
                            receiveTask(task);
                        } else {
                            EventLogger.log("Credenciais inválidas. Tarefa rejeitada.");
                        }

                    } else if (message.startsWith("STATUS_REQUEST:")) {
                        String[] parts = message.split(":", 4);
                        String username = parts[1];
                        String password = parts[2];
                        String taskId = parts[3];

                        if (auth.login(username, password) != null) {
                            Task task = runningTasks.get(taskId);
                            if (task != null) {
                                out.println("Status: " + task.getStatus() + " | Worker: " + task.getAssignedWorker());
                            } else {
                                out.println("Tarefa não encontrada.");
                            }
                        } else {
                            out.println("Credenciais inválidas.");
                        }
                    }
                }
            } catch (IOException e) {
                EventLogger.log("Erro no servidor do orquestrador: " + e.getMessage());
            } catch (Exception e) {
                EventLogger.log("Erro geral no processamento de mensagem: " + e.getMessage());
            }
        }).start();
    }

    private void handleStatusUpdate(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Task updatedTask = mapper.readValue(json, Task.class);

            Task existingTask = runningTasks.get(updatedTask.getId());
            if (existingTask != null) {
                existingTask.setStatus(updatedTask.getStatus());
                EventLogger.log("Status atualizado da tarefa " + updatedTask.getId() + ": " + updatedTask.getStatus());
                replicator.syncState(runningTasks);
            }
        } catch (Exception e) {
            EventLogger.log("Erro ao atualizar status da tarefa: " + e.getMessage());
        }
    }

    public Map<String, Task> getRunningTasks() {
        return runningTasks;
    }
}
