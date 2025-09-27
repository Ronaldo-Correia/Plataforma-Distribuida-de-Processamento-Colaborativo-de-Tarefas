package ifba;

import ifba.orchestrator.BackupOrchestrator;

public class BackupMain {
    public static void main(String[] args) {
        
        BackupOrchestrator backup = new BackupOrchestrator();
        backup.startListening();

        System.out.println("Backup Orchestrator está escutando multicast...");
        
        // Simulação: você pode ativar manualmente o failover
        // backup.becomePrimary();
    }
}
