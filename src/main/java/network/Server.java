package network;

import model.Pessoa;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;



public class
Server {

    private static final int PORT = 22222;
    private static ExecutorService clientTaskPool = Executors.newCachedThreadPool();
    private static SessionFactory sessionFactory;
    private static Map<String, String> emailToSessionMap = new HashMap<>();
    private static Map<String, Pessoa> sessionToUserMap = new HashMap<>();

    public static void main(String[] args) {
        sessionFactory = HibernateUtil.getSessionFactory();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started...");
            while (true) {
                clientTaskPool.execute(new ClientRequestHandler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class ClientRequestHandler implements Runnable {

        private Socket clientSocket;

        public ClientRequestHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String clientRequest;
                while ((clientRequest = inputReader.readLine()) != null) {
                    System.out.println("Received operation from client: " + clientRequest);
                    handleClientRequest(clientRequest, outputWriter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        private void handleClientRequest(String requestData, PrintWriter responseWriter) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode responseNode = mapper.createObjectNode();
                JsonNode requestJson = mapper.readTree(requestData);

                String operationType = requestJson.get("operacao").asText();
                switch (operationType) {
                    case "cadastrarCandidato":
                        registerCandidate(requestJson, responseNode, responseWriter);
                        break;
                    case "loginCandidato":
                        loginUser(requestJson, responseNode, responseWriter);
                        break;
                    case "atualizarCandidato":
                        updateUser(requestJson, responseNode, responseWriter);
                        break;
                    case "visualizarCandidato":
                        visualizarCandidato(requestJson, responseNode, responseWriter);
                        break;
                    case "deletarUsuario":
                        deleteUser(requestJson, responseNode, responseWriter);
                        break;
                    case "logout":
                        logoutUser(requestJson, responseNode, responseWriter);
                        break;
                    default:
                        responseNode.put("status", 400);
                        responseNode.put("mensagem", "Operação inválida");
                        responseWriter.println(responseNode.toString());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                responseWriter.println("{\"status\": 500, \"mensagem\": \"Erro ao processar a operação\"}");
            }
        }

        private void registerCandidate(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String nome = requestData.get("nome").asText();
            String email = requestData.get("email").asText();
            String senha = requestData.get("senha").asText();

            if (isEmailAlreadyExists(email)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "E-mail já cadastrado");
            } else if (!isValidEmail(email)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "Formato de e-mail inválido");
            } else if (!isValidPassword(senha)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "Senha inválida. Deve conter apenas caracteres numéricos e ter entre 3 e 8 caracteres");
            } else if (!isValidName(nome)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "Nome inválido. Deve ter entre 6 e 30 caracteres");
            } else {
                int status = createUser(nome, email, senha);
                responseNode.put("status", status);
                if (status == 201) {
                    String token = UUID.randomUUID().toString();
                    emailToSessionMap.put(email, token);
                    sessionToUserMap.put(token, getUserByEmail(email));
                    responseNode.put("token", token);
                }
            }
            responseWriter.println(responseNode.toString());
        }

        private void deleteUser(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String userEmail = requestData.get("email").asText();  // Recebe o e-mail diretamente do cliente

            if (userEmail == null || userEmail.isEmpty()) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "E-mail necessário para deletar usuário.");
                responseWriter.println(responseNode.toString());
                return;
            }

            try (Session session = sessionFactory.openSession()) {
                Pessoa user = (Pessoa) session.createQuery("FROM pessoa WHERE email = :email")
                        .setParameter("email", userEmail)
                        .uniqueResult();
                if (user != null) {
                    Transaction transaction = session.beginTransaction();
                    session.delete(user);
                    transaction.commit();
                    responseNode.put("operacao","apagarCandidato");
                    responseNode.put("status", 201);
                } else {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Usuário não encontrado.");
                }
            } catch (Exception e) {
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao deletar o usuário.");
                e.printStackTrace();
            }
            responseWriter.println(responseNode.toString());
        }


        private void updateUser(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            // Obtendo o novo nome a partir da solicitação.
            String newName = requestData.path("nome").asText(null);
            String newPassword = requestData.path("senha").asText(null);

            // Verifica se o nome foi fornecido
            if (newName == null || newName.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "Nome e senha são obrigatório para atualização.");
                responseWriter.println(responseNode.toString());
                responseWriter.flush();
                return;
            }
            // Verifica se o nome é válido
            if (!isValidName(newName)) {
                responseNode.put("operacao", "atualizarCandidato");
                responseNode.put("status", 400);
                responseWriter.println(responseNode.toString());
                responseWriter.flush();
                return;
            }

            // Validacao da senha
            if (!isValidPassword(newPassword)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "Senha inválida. Deve conter apenas caracteres numéricos e ter entre 3 e 8 caracteres.");
                responseWriter.println(responseNode.toString());
                responseWriter.flush();
                return;
            }


            // Recupera o email do usuário logado
            String userEmail = requestData.get("email").asText(); // A variável deve ser definida e acessível

            if (userEmail == null || userEmail.isEmpty()) {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "Nenhum usuário está logado atualmente.");
                responseWriter.println(responseNode.toString());
                responseWriter.flush();
                return;
            }

            // Atualizando o usuário no banco de dados
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    Pessoa userToUpdate = (Pessoa) session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                            .setParameter("email", userEmail)
                            .uniqueResult();

                    if (userToUpdate == null) {
                        transaction.rollback();
                        responseNode.put("status", 404);
                        responseNode.put("mensagem", "E-mail não encontrado");
                        responseWriter.println(responseNode.toString());
                        responseWriter.flush();
                        return;
                    }

                    userToUpdate.setNome(newName);  // atualizando o nome
                    userToUpdate.setSenha(newPassword); // atualiza a senha
                    session.saveOrUpdate(userToUpdate);  // atualiza o usuário
                    transaction.commit();
                    responseNode.put("operacao", "atualizarCandidato");
                    responseNode.put("status", 201);
                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                }
            } catch (Exception e) {
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao atualizar o nome do usuário: " + e.getMessage());
                e.printStackTrace();
            } finally {
                responseWriter.println(responseNode.toString());
                responseWriter.flush();
            }
        }





        private void loginUser(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String email = requestData.get("email").asText();
            String senha = requestData.get("senha").asText();

            Pessoa user = getUserByEmailAndPassword(email, senha);
            if (user != null) {
                String token = UUID.randomUUID().toString();
                emailToSessionMap.put(email, token);
                sessionToUserMap.put(email, user);
                responseNode.put("status", 200);
                responseNode.put("token", token);
            } else {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "E-mail ou senha incorretos");
            }
            responseWriter.println(responseNode.toString());
        }

        private void visualizarCandidato(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String email = requestData.get("email").asText();
            Pessoa user = sessionToUserMap.get(email);
            if (user != null) {
                responseNode.put("operacao","visualizarCandidato");
                responseNode.put("status", 201);
                responseNode.put("nome", user.getNome());
                responseNode.put("senha", user.getSenha());
            } else {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "Token de autenticação inválido");
            }
            responseWriter.println(responseNode.toString());
        }

        private void logoutUser(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) throws IOException {
            String token = requestData.get("token").asText();

            if (token == null || token.isEmpty()) {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "Email ou senha incorretos");
                System.out.println("testee");
                responseWriter.println(responseNode.toString());
                return;
            }
            if (sessionToUserMap.containsKey(token)) {
                // Remove o usuário associado ao token
                sessionToUserMap.remove(token);
                // Encontra o email do token
                String userEmail = emailToSessionMap.entrySet().stream()
                        .filter(entry -> token.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

                if (token != null) {
                    // remove usuário e  token
//                    sessionToUserMap.remove(token);
//                    sessionToUserMap.remove(emailToSessionMap);
                    emailToSessionMap.values().remove(token);
                    emailToSessionMap.values().removeIf(existingToken -> existingToken.equals(token));
                }
                responseNode.put("operacao","logout");
                responseNode.put("status", 204);
                responseNode.put("token",token);

            } else {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "Token de autenticação inválido ou sessão já encerrada.");
            }
            responseWriter.println(responseNode.toString());
            responseWriter.flush();

        }





        private boolean isEmailAlreadyExists(String email) {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                        .setParameter("email", email)
                        .uniqueResult() != null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private boolean isValidEmail(String email) {
            String emailRegex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches() && email.length() >= 7 && email.length() <= 50;
        }

        private boolean isValidPassword(String password) {
            return password.matches("\\d+") && password.length() >= 3 && password.length() <= 8;
        }

        private boolean isValidName(String name) {
            return name.length() >= 6 && name.length() <= 30;
        }

        private int createUser(String nome, String email, String senha) {
            Pessoa user = new Pessoa(nome, email, senha);
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
                return 201;
            } catch (Exception e) {
                e.printStackTrace();
                return 500; // HTTP Internal Server Error status code
            }
        }

        private Pessoa getUserByEmailAndPassword(String email, String senha) {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("FROM pessoa WHERE email = :email AND senha = :senha", Pessoa.class)
                        .setParameter("email", email)
                        .setParameter("senha", senha)
                        .uniqueResult();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Pessoa getUserByEmail(String email) {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                        .setParameter("email", email)
                        .uniqueResult();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
