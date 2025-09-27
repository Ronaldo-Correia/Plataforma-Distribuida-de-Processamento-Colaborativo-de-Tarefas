package ifba;

import ifba.common.Task;
import ifba.logs.EventLogger;
import ifba.orchestrator.MainOrchestrator;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MainOrchestrator orchestrator = new MainOrchestrator(false);
        Scanner scanner = new Scanner(System.in);

        orchestrator.startServer(5000);
        
        new Thread(() -> {
    while (true) {
        try (Socket socket = new Socket("localhost", 6000); // porta do backup
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("PRIMARY_HEARTBEAT");
        } catch (Exception ignored) {}
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
}).start();

        // Simula heartbeats de múltiplos workers
        String[] workers = {"worker1", "worker2", "worker3"};
        for (String workerId : workers) {
            new Thread(() -> {
                while (true) {
                    orchestrator.getHeartbeatMonitor().receiveHeartbeat(workerId);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        // Verifica falhas periodicamente
        new Thread(() -> {
            while (true) {
                orchestrator.getHeartbeatMonitor().checkFailures();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Interface de terminal
        while (true) {
            System.out.println("\nComandos:");
            System.out.println("1 - Enviar tarefa");
            System.out.println("2 - Simular falha de worker");
            System.out.println("3 - Ver status das tarefas");
            System.out.println("4 - Sair");
            System.out.print("Escolha: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("ID da tarefa: ");
                    String taskId = scanner.nextLine();
                    System.out.print("Conteúdo da tarefa: ");
                    String payload = scanner.nextLine();

                    Task task = new Task(taskId, payload);
                    orchestrator.receiveTask(task);
                    break;

                case "2":
                    System.out.print("Qual worker deseja simular falha? (worker1, worker2, worker3): ");
                    String targetWorker = scanner.nextLine();
                    System.out.println("Simulando falha: heartbeat de " + targetWorker + " interrompido por 10 segundos...");
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            System.out.println("Heartbeat de " + targetWorker + " retomado.");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    break;

                case "3":
                    System.out.println("Tarefas em execução:");
                    orchestrator.getRunningTasks().forEach((id, t) -> {
                        System.out.println("- " + id + " | Worker: " + t.getAssignedWorker() + " | Status: " + t.getStatus());
                    });
                    break;

                case "4":
                    System.out.println("Encerrando...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Comando inválido.");
            }
        }
    }
    
}
