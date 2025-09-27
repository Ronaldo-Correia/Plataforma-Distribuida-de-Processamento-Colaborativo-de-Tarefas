package ifba.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifba.common.Task;
import ifba.logs.EventLogger;
import ifba.network.AuthManager;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int ORCHESTRATOR_PORT = 5000;
    private static final String ORCHESTRATOR_HOST = "localhost";

    public static void main(String[] args) {
        AuthManager auth = new AuthManager();
        Scanner scanner = new Scanner(System.in);

        String user = null;
        String pass = null;

        while (true) {
            System.out.println("1 - Login");
            System.out.println("2 - Cadastrar novo usuário");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Usuário: ");
                user = scanner.nextLine();
                System.out.print("Senha: ");
                pass = scanner.nextLine();

                String token = auth.login(user, pass);
                if (token == null) {
                    System.out.println("Login inválido.");
                } else {
                    System.out.println("Login bem-sucedido! Token: " + token);
                    break;
                }

            } else if (choice.equals("2")) {
                System.out.print("Novo usuário: ");
                String newUser = scanner.nextLine();
                System.out.print("Nova senha: ");
                String newPass = scanner.nextLine();

                if (auth.register(newUser, newPass)) {
                    System.out.println("Usuário cadastrado com sucesso!");
                } else {
                    System.out.println("Usuário já existe.");
                }

            } else if (choice.equals("3")) {
                System.out.println("Encerrando...");
                scanner.close();
                return;

            } else {
                System.out.println("Opção inválida.");
            }
        }

        while (true) {
            System.out.println("\n1 - Submeter tarefa");
            System.out.println("2 - Consultar status de tarefa");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("ID da tarefa: ");
                String taskId = scanner.nextLine();
                System.out.print("Conteúdo da tarefa: ");
                String payload = scanner.nextLine();

                Task task = new Task(taskId, payload);
                sendTask(task, user, pass);

            } else if (choice.equals("2")) {
                System.out.print("ID da tarefa para consulta: ");
                String taskId = scanner.nextLine();
                consultStatus(taskId, user, pass);

            } else if (choice.equals("3")) {
                System.out.println("Encerrando...");
                break;

            } else {
                System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }

    private static void sendTask(Task task, String user, String pass) {
        try (Socket socket = new Socket(ORCHESTRATOR_HOST, ORCHESTRATOR_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(task);
            out.println("TASK:" + user + ":" + pass + ":" + json);
            EventLogger.log("Tarefa enviada: " + task.getId());
        } catch (Exception e) {
            EventLogger.log("Erro ao enviar tarefa: " + e.getMessage());
        }
    }

    private static void consultStatus(String taskId, String user, String pass) {
        try (Socket socket = new Socket(ORCHESTRATOR_HOST, ORCHESTRATOR_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream())) {

            out.println("STATUS_REQUEST:" + user + ":" + pass + ":" + taskId);
            String response = in.nextLine();
            System.out.println("Resposta do orquestrador: " + response);

        } catch (Exception e) {
            EventLogger.log("Erro ao consultar status: " + e.getMessage());
        }
    }
}
