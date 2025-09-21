package ifba;
import java.util.Scanner;

import ifba.common.Task;
import ifba.logs.EventLogger;
import ifba.orchestrator.MainOrchestrator;

public class Main {
    public static void main(String[] args) {
        MainOrchestrator orchestrator = new MainOrchestrator();
        Scanner scanner = new Scanner(System.in);

        // Thread pra simular heartbeat contínuo no worker1
        new Thread(() -> {
            while (true) {
                orchestrator.getHeartbeatMonitor().receiveHeartbeat("worker1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();

        // Thread pra verificar falhas a cada 3 segundos
        new Thread(() -> {
            while (true) {
                orchestrator.getHeartbeatMonitor().checkFailures();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();

        while (true) {
            System.out.println("\nComandos: ");
            System.out.println("1 - Enviar tarefa");
            System.out.println("2 - Simular falha do worker");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("ID da tarefa: ");
                String taskId = scanner.nextLine();

                System.out.print("Cliente da tarefa: ");
                String client = scanner.nextLine();

                Task task = new Task(taskId, client);
                orchestrator.receiveTask(task);

            } else if (choice.equals("2")) {
                // Para simular a falha, não enviamos heartbeat para worker1
                System.out.println("Simulando falha: heartbeat do worker1 interrompido por 10 segundos...");
                try {
                    Thread.sleep(10000); // pausa no main thread para simular falha
                } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println("Fim da simulação. Heartbeat retomado.");

            } else if (choice.equals("3")) {
                System.out.println("Encerrando...");
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("Comando inválido.");
            }
        }
    }
}
