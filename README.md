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
├── target/                          # Diretório gerado após build (compilados)
└── src/
    └── main/
        └── java/
            └── ifba/                # Pacote raiz da aplicação
                ├── Main.java        # Classe principal com menu interativo
                ├── common/
                │   ├── Task.java                 # Classe que representa uma tarefa
                │   ├── HeartbeatMonitor.java     # Verifica falha de workers via heartbeat
                │   └── LamportClock.java         # Relógio lógico de Lamport para ordenação
                │
                ├── logs/
                │   └── EventLogger.java          # Log centralizado de eventos com timestamp
                │
                ├── orchestrator/
                │   ├── MainOrchestrator.java     # Orquestrador principal (balanceamento, distribuição)
                │   ├── LoadBalancer.java         # Implementação da política de balanceamento
                │   └── StateReplicator.java      # Sincronização de estado (para backup)
                │
                ├── network/
                │   ├── TcpServer.java            # (A ser implementado) Comunicação via TCP
                │   ├── AuthManager.java          # (A ser implementado) Autenticação de usuários
                │   └── MulticastSync.java        # (Opcional) Sincronização via UDP multicast
                │
                └── worker/
                    └── Worker.java               # (A ser implementado) Lógica dos workers (execução, heartbeat)


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
2.  📁Navegue até o local que foi clonado o repositório:
```
cd Plataforma-Distribuida-de-Processamento-Colaborativo-de-Tarefas
```
3. ⚙️ Compile o projeto (caso necessário):

- Se estiver usando Maven, rode:
```
mvn compile
```
4. 🚦 Execute os nós(3 terminais):

Você precisará de 3 terminais abertos. Em cada um, execute um nó com um nodeId, uma porta e a lista de peers (outros nós).

Você precisa abrir 3 terminais separados, um para cada nó.
Cada nó deve ser iniciado com:
```
```

Ou se quiser de maneira mais rápida,no Windows execute os 3 comandos em um único terminal:
```

```
5. 🧪 Testando:

