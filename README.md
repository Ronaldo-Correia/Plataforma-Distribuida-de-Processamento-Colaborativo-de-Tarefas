# Trabalho de Sistemas Distribuídos – Projeto Final - Plataforma Distribuída de Processamento Colaborativo de Tarefas 
- **Instituição:** IFBA - Instituto Federal da Bahia
- **Curso:** Análise e Desenvolvimento de Sistemas (ADS)
- **Disciplina:** Sistemas Distribuídos
- **Professor:** Felipe de Souza Silva
- **Semestre:** 5
- **Ano:** 2025.1

---
## 📌 Projeto Final - Plataforma Distribuída de Processamento Colaborativo de Tarefas 

### Objetivo:
Plataforma distribuída de orquestração de tarefas, que permita a 
submissão de trabalhos por clientes, distribuição para múltiplos nós de processamento 
(workers), acompanhamento do estado global e recuperação em caso de falhas. 
O projeto deve simular um sistema real de processamento colaborativo, aplicando 
conceitos centrais de sistemas distribuídos: balanceamento de carga, consistência de 
estado, tolerância a falhas, replicação, comunicação entre processos e autenticação.

---
## Integrantes do Projeto

<table>
  <tr>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/129338943?v=4" width="100px;" alt="Foto da Integrante Ronaldo"/><br />
      <sub><b><a href="https://github.com/Ronaldo-Correia">Ronaldo Correia</a></b></sub>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/114780494?v=4" width="100px;" alt="Foto da Integrante Marcelo"/><br />
      <sub><b><a href="https://github.com/marceloteclas">Marcelo Jesus</a></b></sub>
    </td>
  </tr>
</table>

---

## 📁Estrutura do Projeto
```
├── pom.xml                          # Arquivo de configuração Maven
├── README.md                        # Documentação do projeto
├── users.txt                        # Arquivo de persistência de usuários
├── target/                          # Diretório gerado após build (compilados)
└── src/
    └── main/
        └── java/
            └── ifba/                # Pacote raiz da aplicação
                ├── Main.java        # Orquestrador principal com menu interativo
                ├── BackupMain.java  # Orquestrador secundário com failover automático
                ├── WorkerMain.java  # Ponto de entrada para os workers
                │
                ├── client/
                │   └── Client.java                  # Interface de linha de comando para o cliente
                │
                ├── common/
                │   ├── Task.java                    # Classe que representa uma tarefa
                │   ├── HeartbeatMonitor.java        # Verifica falha de workers via heartbeat
                │   └── LamportClock.java            # Relógio lógico de Lamport para ordenação
                │
                ├── logs/
                │   └── EventLogger.java             # Log centralizado de eventos com timestamp
                │
                ├── network/
                │   ├── AuthManager.java             # Autenticação de usuários com persistência
                │   └── MulticastSync.java           # Sincronização de estado via multicast (opcional)
                │
                ├── orchestrator/
                │   ├── MainOrchestrator.java        # Orquestrador principal (distribuição, balanceamento)
                │   ├── BackupOrchestrator.java      # Lógica de failover e recuperação de estado
                │   ├── LoadBalancer.java            # Implementação da política de balanceamento
                │   └── StateReplicator.java         # Sincronização de estado entre orquestradores
                │
                └── worker/
                    ├── Worker.java                  # Lógica de execução de tarefas
                    └── WorkerNode.java              # Comunicação e heartbeat com o orquestrador

```

---
## 🚀 Requisitos

- Java 21
- Maven 3.8+

---

## 👨‍💻Como Executar
1. 📥Clone este repositório:
```bash
git clone https://github.com/Ronaldo-Correia/Plataforma-Distribuida-de-Processamento-Colaborativo-de-Tarefas.git
```
2. 📁Navegue até o diretório do projeto:
```bash
cd "C:\Users\Documents\Plataforma-Distribuida-de-Processamento-Colaborativo-de-Tarefas"
```

3. ⚙️ Compile o projeto:
```bash
mvn compile
```

4. 📦 Empacote o projeto (cria o JAR com dependências):
```bash
mvn package
```
---

## 🧪 5. Testando a Plataforma Distribuída
Abrir múltiplos terminais no Windows
Você pode usar:
Windows Terminal (melhor opção: várias abas)
CMD ou PowerShell
Ou abrir múltiplas janelas manualmente

✅ Executar os 6 componentes
🖥️ Terminal 1 — Orquestrador Principal
```bash
java -jar target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

🖥️ Terminal 2 — Orquestrador Backup (com failover automático)
```bash
java -cp "target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar" ifba.BackupMain
```

🖥️ Terminal 3 — Worker 1
```bash
java -cp "target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar" ifba.WorkerMain worker1 6001
```

🖥️ Terminal 4 — Worker 2
```bash
java -cp "target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar" ifba.WorkerMain worker2 6002
```

🖥️ Terminal 5 — Worker 3
```bash
java -cp "target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar" ifba.WorkerMain worker3 6003
```

🖥️ Terminal 6 — Cliente
```bash
java -cp "target/distributed-orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar" ifba.client.Client
```
---
## 6. 📝 Notas Finais

- ✅ A autenticação de usuários é feita via arquivo users.txt, com persistência local.
- 🔄 O orquestrador backup assume automaticamente se o principal falhar (failover automático).
- 📋 Logs de eventos são exibidos no console com timestamps para facilitar o monitoramento.
- ⚙️ A plataforma é modular e pode ser expandida com mais workers e clientes.
- 📁 O repositório contém README detalhado com instruções de instalação, execução e exemplos de uso.
- 🧠 O código está organizado em pacotes funcionais, facilitando manutenção e evolução.
