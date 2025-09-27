package ifba.worker;

import ifba.common.Task;
import ifba.logs.EventLogger;

import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class Worker {
    private final String workerId;
    private final int port;
    private boolean running = true;

    public Worker(String workerId, int port) {
        this.workerId = workerId;
        this.port = port;
    }

    public void start() {
        startHeartbeat();
        listenForTasks();
    }

    private void startHeartbeat() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try (Socket socket = new Socket("localhost", 5000); // Porta do orquestrador
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println("HEARTBEAT:" + workerId);
                } catch (IOException e) {
                    EventLogger.log("Erro ao enviar heartbeat: " + e.getMessage());
                }
            }
        }, 0, 2000); // Envia a cada 2 segundos
    }

    private void listenForTasks() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            EventLogger.log("Worker " + workerId + " aguardando tarefas na porta " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String json = in.readLine();

                Task task = parseTask(json);
                executeTask(task);
            }
        } catch (IOException e) {
            EventLogger.log("Erro no worker " + workerId + ": " + e.getMessage());
        }
    }

    private Task parseTask(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Task.class);
        } catch (Exception e) {
            EventLogger.log("Erro ao interpretar tarefa: " + e.getMessage());
            return null;
        }
    }

    private void executeTask(Task task) {
        if (task == null) return;

        EventLogger.log("Worker " + workerId + " iniciou tarefa " + task.getId());
        task.setStatus(Task.Status.RUNNING);
        task.setAssignedWorker(workerId);

        try {
            Thread.sleep(3000); // Simula execução
            task.setStatus(Task.Status.COMPLETED);
            EventLogger.log("Worker " + workerId + " concluiu tarefa " + task.getId());
        } catch (InterruptedException e) {
            task.setStatus(Task.Status.FAILED);
            EventLogger.log("Worker " + workerId + " falhou na tarefa " + task.getId());
        }

        reportStatus(task);
    }

    private void reportStatus(Task task) {
        try (Socket socket = new Socket("localhost", 5000); // Porta do orquestrador
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(task);
            out.println("STATUS:" + json);
        } catch (IOException e) {
            EventLogger.log("Erro ao reportar status: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Worker("worker1", 6001).start(); // Exemplo: worker1 escuta na porta 6001
    }
}