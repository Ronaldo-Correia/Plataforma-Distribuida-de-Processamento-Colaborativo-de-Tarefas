package ifba;

import ifba.orchestrator.MainOrchestrator;

public class BackupMain {
    public static void main(String[] args) {
        
        MainOrchestrator backup = new MainOrchestrator(true);
new Thread(() -> {
    while (true) {
        if (!backup.isPrimaryAlive()) {
            System.out.println("[FAILOVER] Orquestrador principal caiu. Assumindo papel de coordenador.");
            backup.startServer(5000); // assume a porta do principal
            break;
        }
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
}).start();

        backup.startServer(6000);

        System.out.println("Backup Orchestrator está escutando na porta 6000...");

    }
}
