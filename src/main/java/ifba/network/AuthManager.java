package ifba.network;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthManager {
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>();
    private static final String USER_FILE = System.getProperty("user.dir") + File.separator + "users.txt";

    public AuthManager() {
        loadUsers();
    }

    public String login(String username, String password) {
    loadUsers(); 
    if (users.containsKey(username) && users.get(username).equals(password)) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }
    return null;
}


    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        saveUsers();
        return true;
    }

    public boolean isValidToken(String token) {
        return tokens.containsKey(token);
    }

    public String getUser(String token) {
        return tokens.get(token);
    }

    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }
}
