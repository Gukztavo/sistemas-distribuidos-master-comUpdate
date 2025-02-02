package network;

import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import model.*;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import service.CompetenciaService;



public class
Server {

    private static final int PORT = 22222;
    private static ExecutorService clientTaskPool = Executors.newCachedThreadPool();
    private static SessionFactory sessionFactory;
    private static Map<String, String> emailToSessionMap = new HashMap<>();
    private static Map<String, Pessoa> sessionToUserMap = new HashMap<>();

    private static Map<String, String> emailToSessionMapEmp = new HashMap<>();
    private static Map<String, Empresa> sessionToUserMapEmp = new HashMap<>();


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



//        private void handleClientRequest(String requestData, PrintWriter responseWriter) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                ObjectNode responseNode = mapper.createObjectNode();
//                JsonNode requestJson = mapper.readTree(requestData);
//
//                String operationType = requestJson.get("operacao").asText();
//
//                if (operationType.startsWith("Candidato")) {
//                    handleCandidatoRequests(operationType, requestJson, responseNode, responseWriter);
//                } else if (operationType.startsWith("Empresa")) {
//                    handleEmpresaRequests(operationType, requestJson, responseNode, responseWriter);
//                } else {
//                    responseNode.put("status", 400);
//                    responseNode.put("mensagem", "Operação inválida");
//                    responseWriter.println(responseNode.toString());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                responseWriter.println("{\"status\": 500, \"mensagem\": \"Erro ao processar a operação\"}");
//            }
//        }

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
                    case "apagarCandidato":
                        apagarCandidato(requestJson, responseNode, responseWriter);
                        break;
                    case "logout", "logoutEmpresa":
                        logoutUser(requestJson, responseNode, responseWriter);
                        break;
                    case "cadastrarEmpresa":
                        cadastrarEmpresa(requestJson, responseNode, responseWriter);
                        break;
                    case "loginEmpresa":
                        loginEmpresa(requestJson, responseNode, responseWriter);
                        break;
                    case "atualizarEmpresa":
                        updateEmpresa(requestJson, responseNode, responseWriter);
                        break;
                    case "visualizarEmpresa":
                        visualizarEmpresa(requestJson, responseNode, responseWriter);
                        break;
                    case "apagarEmpresa":
                        apagarEmpresa(requestJson, responseNode, responseWriter);
                        break;
                    case "cadastrarCompetenciaExperiencia":
                        cadastrarCompetenciaExperiencia(requestJson, responseNode, responseWriter);
                        break;
                    case "visualizarCompetenciaExperiencia":
                        visualizarCompetenciaExperiencia(requestJson, responseNode, responseWriter);
                        break;
                    case "atualizarCompetenciaExperiencia":
                        atualizarCompetenciaExperiencia(requestJson, responseNode, responseWriter);
                        break;
                    case "apagarCompetenciaExperiencia":
                        apagarCompetenciaExperiencia(requestJson, responseNode, responseWriter);
                        break;
                    case "cadastrarVaga":
                        cadastrarVaga(requestJson, responseNode, responseWriter);
                        break;
                    case "visualizarVaga":
                        visualizarVaga(requestJson, responseNode, responseWriter);
                        break;
                    case "atualizarVaga":
                        atualizarVaga(requestJson, responseNode, responseWriter);
                        break;
                    case "apagarVaga":
                        apagarVaga(requestJson, responseNode, responseWriter);
                        break;
                    case "listarVagas":
                        listarVagas(requestJson, responseNode, responseWriter);
                        break;
                    case "filtrarVagas":
                        filtrarVagas(requestJson, responseNode, responseWriter);
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

//        private void handleEmpresaRequests(String requestData, PrintWriter responseWriter) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                ObjectNode responseNode = mapper.createObjectNode();
//                JsonNode requestJson = mapper.readTree(requestData);
//
//                String operationType = requestJson.get("operacao").asText();
//                switch (operationType) {
//                    case "cadastrarEmpresa":
//                        registrarEmpresa(requestJson, responseNode, responseWriter);
//                        break;
//                    case "loginEmpresa":
//                        loginEmpresa(requestJson, responseNode, responseWriter);
//                        break;
//                    case "atualizarEmpresa": // Certifique-se de ter um método correspondente
//                        updateEmpresa(requestJson, responseNode, responseWriter);
//                        break;
//                    case "visualizarEmpresa": // Certifique-se de ter um método correspondente
//                        visualizarEmpresa(requestJson, responseNode, responseWriter);
//                        break;
//                    case "apagarEmpresa": // Certifique-se de ter um método correspondente
//                        apagarEmpresa(requestJson, responseNode, responseWriter);
//                        break;
//                    case "logoutEmpresa":
//                        logoutUser(requestJson, responseNode, responseWriter);
//                        break;
//                    default:
//                        responseNode.put("status", 400);
//                        responseNode.put("mensagem", "Operação inválida");
//                        responseWriter.println(responseNode.toString());
//                        break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                responseWriter.println("{\"status\": 500, \"mensagem\": \"Erro ao processar a operação\"}");
//            }
//        }


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
                responseNode.put("operacao","cadastrarCandidato");
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

        private void apagarCandidato(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String userEmail = requestData.get("email").asText();  // Recebe o e-mail diretamente do cliente

            if (userEmail == null || userEmail.isEmpty() || !isValidEmail(userEmail)) {
                responseNode.put("status", 400);
                responseNode.put("mensagem", "E-mail necessário para deletar usuário é inválido ou não foi fornecido.");
                responseWriter.println(responseNode.toString());
                return;
            }

            try (Session session = sessionFactory.openSession()) {
                Pessoa user = (Pessoa) session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                        .setParameter("email", userEmail)
                        .uniqueResult();
                if (user != null) {
                    Transaction transaction = session.beginTransaction();
                    session.delete(user);
                    transaction.commit();
                    responseNode.put("status", 200);
                    responseNode.put("operacao", "apagarCandidato");
                    responseNode.put("mensagem", "Usuário deletado com sucesso.");
                } else {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Usuário não encontrado.");
                }
            } catch (Exception e) {
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao deletar o usuário: " + e.getMessage());
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
                sessionToUserMap.put(token, user);
                responseNode.put("operacao", "loginCandidato");
                responseNode.put("status", 200);
                responseNode.put("token", token);
                responseNode.put("email", email);  // Incluindo o email na resposta
            } else {
                responseNode.put("status", 401);
                responseNode.put("mensagem", "E-mail ou senha incorretos");
            }
            responseWriter.println(responseNode.toString());
        }

        private void visualizarCandidato(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
            String email = requestData.get("email").asText();

            // Verifica se existe uma sessão válida para o email fornecido
            if (emailToSessionMap.containsKey(email)) {
                String token = emailToSessionMap.get(email);  // Obtém o token a partir do email
                Pessoa user = sessionToUserMap.get(token);  // Obtém o usuário a partir do token

                if (user != null) {
                    responseNode.put("operacao", "visualizarCandidato");
                    responseNode.put("status", 201);
                    responseNode.put("nome", user.getNome());
                    responseNode.put("senha", user.getSenha());
                } else {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Usuário não encontrado");
                }
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
                responseNode.put("mensagem", "Token de autenticação não fornecido.");
                responseWriter.println(responseNode.toString());
                return;
            }

            if (sessionToUserMap.containsKey(token) || sessionToUserMapEmp.containsKey(token)) {
                // Remove o usuário associado ao token
                sessionToUserMap.remove(token);
                sessionToUserMapEmp.remove(token);

                // Remove o token do mapa de emails
                emailToSessionMap.values().remove(token);
                emailToSessionMapEmp.values().remove(token);

                responseNode.put("operacao", "logout");
                responseNode.put("status", 204);
                responseNode.put("token", token);
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
    //--------------------------------vagas-----------------------------------------------
    private static void cadastrarVaga(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String nome = requestData.get("nome").asText();
        String email = requestData.get("email").asText();
        int faixaSalarial = requestData.get("faixaSalarial").asInt();
        String descricao = requestData.get("descricao").asText();
        String estado = requestData.get("estado").asText();
        String token = requestData.get("token").asText();

        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

       // String token = emailToSessionMapEmp.get(email);  // Obtém o token a partir do email
        Empresa empresa = sessionToUserMapEmp.get(token);
        if (empresa == null) {
            responseNode.put("status", 404);
            responseNode.put("mensagem", "Empresa não encontrada");
            responseWriter.println(responseNode.toString());
            return;
        }

        List<String> competencias = new ArrayList<>();
        for (JsonNode comp : requestData.get("competencias")) {
            competencias.add(comp.asText());
        }
        Empresa empresaum = new Empresa();
        empresaum.setEmail(email);
        Vaga vaga = new Vaga();
        vaga.setNome(nome);

        vaga.setFaixaSalarial(faixaSalarial);
        vaga.setDescricao(descricao);
        vaga.setEstado(estado);
        vaga.setEmpresa(empresa);
        vaga.setCompetencias(competencias);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(vaga);
                transaction.commit();
                responseNode.put("operacao", "cadastrarVaga");
                responseNode.put("status", 201);
                responseNode.put("mensagem", "Vaga cadastrada com sucesso");
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao cadastrar vaga: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados");
            e.printStackTrace();
        }

        responseWriter.println(responseNode.toString());
    }

    private static void visualizarVaga(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        Long idVaga = requestData.get("idVaga").asLong();
        String token = requestData.get("token").asText();
        Empresa empresa = sessionToUserMapEmp.get(token);

        // Verificar se o token é válido
        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Vaga vaga = session.createQuery("FROM Vaga WHERE id = :idVaga AND empresa.email = :email", Vaga.class)
                    .setParameter("idVaga", idVaga)
                    .setParameter("email", email)
                    .uniqueResult();

            if (vaga == null) { //estorou esse erro
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Vaga não encontrada ou não pertence à empresa");
                responseWriter.println(responseNode.toString());
                return;
            }

            responseNode.put("operacao", "visualizarVaga");
            responseNode.put("status", 200);
            responseNode.put("id", vaga.getId());
            responseNode.put("nome", vaga.getNome());
            responseNode.put("faixaSalarial", vaga.getFaixaSalarial());
            responseNode.put("descricao", vaga.getDescricao());
            responseNode.put("estado", vaga.getEstado());
            responseNode.put("email", vaga.getEmpresa().getEmail());

            ArrayNode competenciasArray = responseNode.putArray("competencias");
            for (String competencia : vaga.getCompetencias()) {
                competenciasArray.add(competencia);
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao visualizar a vaga: " + e.getMessage());
            e.printStackTrace();
        }

        responseWriter.println(responseNode.toString());
    }

//    private static void atualizarVaga(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
//        String email = requestData.get("email").asText();
//        String token = requestData.get("token").asText();
//        int idVaga = requestData.get("idVaga").asInt();
//
//        String novoNome = requestData.get("nome").asText();
//        int novaFaixaSalarial = requestData.get("faixaSalarial").asInt();
//        String novaDescricao = requestData.get("descricao").asText();
//        String novoEstado = requestData.get("estado").asText();
//        ArrayNode competenciasArray = (ArrayNode) requestData.get("competencias");
//
//        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
//            responseNode.put("status", 401);
//            responseNode.put("mensagem", "Token de autenticação inválido");
//            responseWriter.println(responseNode.toString());
//            return;
//        }
//
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            try {
//                Vaga vaga = session.createQuery("FROM Vaga WHERE id = :idVaga AND empresa.email = :email", Vaga.class)
//                        .setParameter("idVaga", idVaga)
//                        .setParameter("email", email)
//                        .uniqueResult();
//
//                if (vaga == null) {
//                    responseNode.put("status", 404);
//                    responseNode.put("mensagem", "Vaga não encontrada");
//                    responseWriter.println(responseNode.toString());
//                    return;
//                }
//
//                // Atualizar campos da vaga
//                vaga.setNome(novoNome);
//                vaga.setFaixaSalarial(novaFaixaSalarial);
//                vaga.setDescricao(novaDescricao);
//                vaga.setEstado(novoEstado);
//                session.merge(vaga); // Usar merge em vez de saveOrUpdate
//
//                // Remover competências antigas
//                session.createQuery("DELETE FROM VagaCompetencia WHERE vaga.id = :idVaga")
//                        .setParameter("idVaga", vaga.getId())
//                        .executeUpdate();
//
//                // Adicionar novas competências
//                for (JsonNode competenciaNode : competenciasArray) {
//                    VagaCompetencia vagaCompetencia = new VagaCompetencia();
//                    vagaCompetencia.setVaga(vaga);
//                    vagaCompetencia.setCompetencia(competenciaNode.asText());
//                    session.persist(vagaCompetencia); // Garantir a criação de um novo registro
//                }
//
//                transaction.commit();
//                responseNode.put("status", 201);
//                responseNode.put("token",token);
//                responseNode.put("mensagem", "Vaga atualizada com sucesso");
//            } catch (Exception e) {
//                transaction.rollback();
//                responseNode.put("status", 500);
//                responseNode.put("mensagem", "Erro ao atualizar a vaga: " + e.getMessage());
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            responseNode.put("status", 500);
//            responseNode.put("mensagem", "Erro ao conectar ao banco de dados");
//        }
//        responseWriter.println(responseNode.toString());
//    }
private static void atualizarVaga(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();
        int idVaga = requestData.get("idVaga").asInt();

        String novoNome = requestData.get("nome").asText();
        double novaFaixaSalarial = requestData.get("faixaSalarial").asDouble();
        String novaDescricao = requestData.get("descricao").asText();
        String novoEstado = requestData.get("estado").asText();
        ArrayNode competenciasArray = (ArrayNode) requestData.get("competencias");

        // Verificar se o token é válido
        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Vaga vaga = session.createQuery("FROM Vaga WHERE id = :idVaga AND empresa.email = :email", Vaga.class)
                        .setParameter("idVaga", idVaga)
                        .setParameter("email", email)
                        .uniqueResult();

                if (vaga == null) {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Vaga não encontrada");
                    responseWriter.println(responseNode.toString());
                    return;
                }

                // Atualizar campos da vaga
                vaga.setNome(novoNome);
                vaga.setFaixaSalarial(novaFaixaSalarial);
                vaga.setDescricao(novaDescricao);
                vaga.setEstado(novoEstado);

                // Remover competências antigas
                List<VagaCompetencia> competenciasAntigas = session.createQuery("FROM VagaCompetencia WHERE vaga.id = :idVaga", VagaCompetencia.class)
                        .setParameter("idVaga", idVaga)
                        .list();
                for (VagaCompetencia competenciaAntiga : competenciasAntigas) {
                    session.delete(competenciaAntiga);
                }

                // Adicionar novas competências
                for (JsonNode competenciaNode : competenciasArray) {
                    VagaCompetencia vagaCompetencia = new VagaCompetencia();
                    vagaCompetencia.setVaga(vaga);
                    vagaCompetencia.setCompetencia(competenciaNode.asText());
                    session.save(vagaCompetencia);
                }

                session.saveOrUpdate(vaga);
                transaction.commit();

                responseNode.put("operacao", "atualizarVaga");
                responseNode.put("status", 201);
                responseNode.put("mensagem", "Vaga atualizada com sucesso");
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao atualizar a vaga: " + e.getMessage());
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados");
        }

        responseWriter.println(responseNode.toString());
    }










    private static void apagarVaga(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();
        int idVaga = requestData.get("idVaga").asInt();

        // Verificar se o token é válido
        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Vaga vaga = session.createQuery("FROM Vaga v WHERE v.id = :idVaga AND v.empresa.email = :email", Vaga.class)
                        .setParameter("idVaga", idVaga)
                        .setParameter("email", email)
                        .uniqueResult();

                if (vaga == null) {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Vaga não encontrada");
                    responseWriter.println(responseNode.toString());
                    return;
                }

                session.delete(vaga);
                transaction.commit();

                responseNode.put("operacao", "apagarVaga");
                responseNode.put("status", 201);
                responseNode.put("mensagem", "Vaga apagada com sucesso");
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao apagar a vaga: " + e.getMessage());
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados");
        }

        responseWriter.println(responseNode.toString());
    }

    private static void listarVagas(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();

        try (Session session = sessionFactory.openSession()) {
            List<Vaga> vagas = session.createQuery("SELECT DISTINCT v FROM Vaga v WHERE v.empresa.email = :email", Vaga.class)
                    .setParameter("email", email)
                    .list();

            ArrayNode vagasArray = responseNode.putArray("vagas");
            for (Vaga vaga : vagas) {
                ObjectNode vagaNode = vagasArray.addObject();
                vagaNode.put("nome", vaga.getNome());
                vagaNode.put("idVaga", vaga.getId());
            }

            responseNode.put("operacao", "listarVagas");
            responseNode.put("status", 201);
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao listar vagas: " + e.getMessage());
            e.printStackTrace();
        }
        responseWriter.println(responseNode.toString());
    }

    private static void filtrarVagas(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String token = requestData.get("token").asText();
        JsonNode filtrosNode = requestData.get("filtros");
        ArrayNode competenciasNode = (ArrayNode) filtrosNode.get("competencias");
        String tipo = filtrosNode.get("tipo").asText();

        // Verificar se o token é válido
        if (!emailToSessionMap.containsValue(token) && !emailToSessionMap.containsValue(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        List<String> competencias = new ArrayList<>();
        for (JsonNode competenciaNode : competenciasNode) {
            competencias.add(competenciaNode.asText());
        }

        try (Session session = sessionFactory.openSession()) {
            String queryStr = "SELECT DISTINCT v FROM Vaga v JOIN v.competencias c WHERE c IN :competencias";
            if (tipo.equalsIgnoreCase("AND")) {
                queryStr += " GROUP BY v.id HAVING COUNT(DISTINCT c) = :numCompetencias";
            }
            org.hibernate.query.Query<Vaga> query = session.createQuery(queryStr, Vaga.class);
            query.setParameter("competencias", competencias);
            if (tipo.equalsIgnoreCase("AND")) {
                query.setParameter("numCompetencias", competencias.size());
            }

            List<Vaga> vagas = query.list();
            ArrayNode vagasArray = responseNode.putArray("vagas");

            for (Vaga vaga : vagas) {
                ObjectNode vagaNode = vagasArray.addObject();
                vagaNode.put("idVaga", vaga.getId());
                vagaNode.put("nome", vaga.getNome());
                vagaNode.put("faixaSalarial", vaga.getFaixaSalarial());
                vagaNode.put("descricao", vaga.getDescricao());
                vagaNode.put("estado", vaga.getEstado());
                vagaNode.put("email", vaga.getEmpresa().getEmail());

                ArrayNode competenciasArray = vagaNode.putArray("competencias");
                for (String competencia : vaga.getCompetencias()) {
                    competenciasArray.add(competencia);
                }
            }

            responseNode.put("operacao", "filtrarVagas");
            responseNode.put("status", 201);
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao filtrar vagas: " + e.getMessage());
            e.printStackTrace();
        }
        responseWriter.println(responseNode.toString());
    }






    //--------------------------------Empresa---------------------------------------------


    private static void cadastrarEmpresa(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String razaoSocial = requestData.get("razaoSocial").asText();
        String email= requestData.get("email").asText();
        String cnpj = requestData.get("cnpj").asText();
        String senha = requestData.get("senha").asText();
        String descricao = requestData.get("descricao").asText();
        String ramo = requestData.get("ramo").asText();

        if (isEmailAlreadyExists(email) || isCNPJAlreadyExists(cnpj)) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "E-mail ou CNPJ já cadastrado");
        } else if (!isValidEmail(email) || !isValidCNPJ(cnpj) || !isValidPassword(senha)) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "Dados inválidos. Verifique o e-mail, CNPJ ou senha.");
        } else {
            int status = createEmpresa(razaoSocial, email, cnpj, senha, descricao, ramo);
            if (status == 201) {
                String token = UUID.randomUUID().toString();
                emailToSessionMapEmp.put(email, token);
                sessionToUserMapEmp.put(token, new Empresa(razaoSocial, email, cnpj, senha, descricao, ramo));
                responseNode.put("status", 201);
                responseNode.put("token", token);
            } else {
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao registrar a empresa.");
            }
        }
        responseNode.put("operacao","cadastrarEmpresa");
        responseWriter.println(responseNode.toString());
    }
    private static void visualizarEmpresa(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();

        if (!emailToSessionMapEmp.containsKey(email) || !emailToSessionMapEmp.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Empresa empresa = session.createQuery("FROM empresa WHERE email = :email", Empresa.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (empresa == null) {
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Empresa não encontrada");
            } else {
                responseNode.put("status", 200);
                responseNode.put("operacao", "visualizarEmpresa");
                responseNode.put("razaoSocial", empresa.getRazaoSocial());
                responseNode.put("email", empresa.getEmail());
                responseNode.put("cnpj", empresa.getCnpj());
                responseNode.put("descricao", empresa.getDescricao());
                responseNode.put("ramo", empresa.getRamo());
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao visualizar empresa: " + e.getMessage());
            e.printStackTrace();
        }
        responseWriter.println(responseNode.toString());
    }


    private static void updateEmpresa(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        // Obtendo os novos dados a partir da solicitação.
        String token = requestData.get("token").asText();
        String newRazaoSocial = requestData.path("razaoSocial").asText(null);
        String newEmail = requestData.path("email").asText(null);
        String newCnpj = requestData.path("cnpj").asText(null);
        String newPassword = requestData.path("senha").asText(null);
        String newDescricao = requestData.path("descricao").asText(null);
        String newRamo = requestData.path("ramo").asText(null);
        String emailAtual = requestData.path("email").asText(null);
        String userEmail = requestData.get("email").asText();
        // Verifica se os dados foram fornecidos e são válidos.
        if (newRazaoSocial == null || newRazaoSocial.isEmpty() ||
                newEmail == null || newEmail.isEmpty()||
                newCnpj == null || newCnpj.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                newDescricao == null || newDescricao.isEmpty() ||
                newRamo == null || newRamo.isEmpty() ||
                userEmail == null || emailAtual.isEmpty()) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "Todos os campos são obrigatórios para atualização.");
            responseWriter.println(responseNode.toString());
            return;
        }

        // Validação da senha
        if (!isValidPassword(newPassword)) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "Senha inválida. Deve conter apenas caracteres numéricos e ter entre 3 e 8 caracteres.");
            responseWriter.println(responseNode.toString());
            return;
        }

        // Atualizando a empresa no banco de dados
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Empresa empresaToUpdate = (Empresa) session.createQuery("FROM empresa WHERE email = :emailAtual", Empresa.class)
                        .setParameter("emailAtual", emailAtual)
                        .uniqueResult();

                if (empresaToUpdate == null) {
                    transaction.rollback();
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Empresa não encontrada com o email fornecido.");
                    responseWriter.println(responseNode.toString());
                    return;
                }

                empresaToUpdate.setRazaoSocial(newRazaoSocial);
                empresaToUpdate.setEmail(newEmail);
                empresaToUpdate.setCnpj(newCnpj);
                empresaToUpdate.setSenha(newPassword);
                empresaToUpdate.setDescricao(newDescricao);
                empresaToUpdate.setRamo(newRamo);

                session.saveOrUpdate(empresaToUpdate);
                transaction.commit();
                responseNode.put("operacao", "atualizarEmpresa");
                responseNode.put("token",token);
                responseNode.put("status", 201);
                responseNode.put("mensagem", "Dados da empresa atualizados com sucesso.");
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao atualizar os dados da empresa: " + e.getMessage());
                responseWriter.println(responseNode.toString());
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados.");
            e.printStackTrace();
            responseWriter.println(responseNode.toString());
            return;
        }
        responseWriter.println(responseNode.toString());

    }

    private static void apagarEmpresa(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String empEmail = requestData.get("email").asText(); // Correto: usando "email" como chave
        String token = requestData.get("token").asText();
        if (empEmail == null || empEmail.isEmpty()) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "E-mail necessário para deletar empresa é inválido ou não foi fornecido.");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Empresa empresa = session.createQuery("FROM empresa WHERE email = :email", Empresa.class)
                    .setParameter("email", empEmail)
                    .uniqueResult();
            if (empresa != null) {
                Transaction transaction = session.beginTransaction();
                session.delete(empresa);
                transaction.commit();
                responseNode.put("status", 201);
                responseNode.put("token",token);
                responseNode.put("operacao", "apagarEmpresa");

                responseNode.put("mensagem", "Empresa deletada com sucesso.");
            } else {
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Empresa não encontrada.");
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao deletar a empresa: " + e.getMessage());
            e.printStackTrace();
        }
        responseWriter.println(responseNode.toString());
    }




    private static boolean isEmailAlreadyExists(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT 1 FROM Empresa WHERE email = :email")
                    .setParameter("email", email)
                    .uniqueResult() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static boolean isValidEmail(String email) {
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches() && email.length() >= 7 && email.length() <= 50;
    }

   private boolean isValidPassword(String password) {
    return password.matches("[a-zA-Z0-9@#_]+") && password.length() >= 6 && password.length() <= 20;
    }


    private static boolean isValidCNPJ(String cnpj) {

        return cnpj.matches("\\d{14}");
    }

    private static boolean isCNPJAlreadyExists(String cnpj) {
        try (Session session = sessionFactory.openSession()) {
            Long count = (Long) session.createQuery("SELECT count(e) FROM empresa e WHERE e.cnpj = :cnpj")
                    .setParameter("cnpj", cnpj)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void cadastrarCompetenciaExperiencia(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();

        if (emailToSessionMap.containsKey(email) && emailToSessionMap.get(email).equals(token)) {
            Pessoa user = sessionToUserMap.get(token);

            if (user != null) {
                try (Session session = sessionFactory.openSession()) {
                    Transaction transaction = session.beginTransaction();

                    ArrayNode competenciasArray = (ArrayNode) requestData.get("competenciaExperiencia");
                    for (JsonNode competenciaNode : competenciasArray) {
                        String competencia = competenciaNode.get("competencia").asText();
                        int experiencia = competenciaNode.get("experiencia").asInt();

                        if (CompetenciaService.isCompetenciaValida(competencia)) {
                            CompetenciaExperiencia competenciaExperiencia = new CompetenciaExperiencia();
                            competenciaExperiencia.setCompetencia(competencia);
                            competenciaExperiencia.setExperiencia(experiencia);
                            competenciaExperiencia.setCandidato(user);

                            session.save(competenciaExperiencia);session.save(competenciaExperiencia);
                        } else {
                            responseNode.put("status", 400);
                            responseNode.put("mensagem", "Competência inválida: " + competencia);
                            responseWriter.println(responseNode.toString());
                            return;
                        }
                    }

                    transaction.commit();
                    responseNode.put("status", 201);
                    responseNode.put("operacao", "cadastrarCompetenciaExperiencia");
                    responseNode.put("token",token);
                    responseNode.put("mensagem", "Competências cadastradas com sucesso");
                } catch (Exception e) {
                    responseNode.put("status", 500);
                    responseNode.put("mensagem", "Erro ao cadastrar competências e experiências: " + e.getMessage());
                }
            } else {
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Usuário não encontrado");
            }
        } else {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
        }

        responseWriter.println(responseNode.toString());
    }



    private static void visualizarCompetenciaExperiencia(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();

        // Verificar se o token é válido
        if (!emailToSessionMap.containsKey(email) || !emailToSessionMap.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Pessoa candidato = session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (candidato == null) {
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Candidato não encontrado");
                responseWriter.println(responseNode.toString());
                return;
            }else {

            List<CompetenciaExperiencia> competenciasExperiencias = session.createQuery("FROM CompetenciaExperiencia WHERE candidato = :candidato",
                            CompetenciaExperiencia.class)
                    // CompetenciaExperiencia competenciaExperiencia = session.createQuery("FROM CompetenciaExperiencia WHERE candidato.email = :email AND competencia = :competencia", CompetenciaExperiencia.class)


                    .setParameter("candidato", candidato)
                    .list();

            responseNode.put("status", 201);
            responseNode.put("operacao", "visualizarCompetenciaExperiencia");
            responseNode.put("email", email);
            responseNode.put("token",token);

            ArrayNode competenciasArray = responseNode.putArray("competenciaExperiencia");
            for (CompetenciaExperiencia ce : competenciasExperiencias) {
                ObjectNode ceNode = competenciasArray.addObject();
                ceNode.put("competencia", ce.getCompetencia());
                ceNode.put("experiencia", ce.getExperiencia());
                }
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao visualizar competências e experiências: " + e.getMessage());
            e.printStackTrace();
        }

        responseWriter.println(responseNode.toString());
    }


    private static void atualizarCompetenciaExperiencia(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.path("email").asText(null);
        String token = requestData.path("token").asText(null);
        JsonNode competenciasExperiencias = requestData.path("competenciaExperiencia");

        if (email == null || token == null || !requestData.has("competenciaExperiencia") || competenciasExperiencias.isEmpty()) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "Dados incompletos ou incorretos.");
            responseWriter.println(responseNode.toString());
            return;
        }

        if (!emailToSessionMap.containsKey(email) || !emailToSessionMap.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Pessoa candidato = session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                        .setParameter("email", email)
                        .uniqueResult();

                if (candidato == null) {
                    responseNode.put("status", 404);
                    responseNode.put("mensagem", "Candidato não encontrado");
                    transaction.rollback();
                } else {
                    for (JsonNode compExpNode : competenciasExperiencias) {
                        String competencia = compExpNode.path("competencia").asText(null);
                        int experiencia = compExpNode.path("experiencia").asInt(0); // Assume zero se inválido

                        CompetenciaExperiencia ce = session.createQuery("FROM CompetenciaExperiencia WHERE competencia = :competencia AND candidato = :candidato", CompetenciaExperiencia.class)
                                .setParameter("competencia", competencia)
                                .setParameter("candidato", candidato)
                                .uniqueResult();

                        if (ce != null) {
                            ce.setExperiencia(experiencia);
                            session.update(ce);
                        } else {
                            responseNode.put("status", 404);
                            responseNode.put("mensagem", "Competência especificada não encontrada: " + competencia);
                            transaction.rollback();
                            responseWriter.println(responseNode.toString());
                            return;
                        }
                    }
                    transaction.commit();
                    responseNode.put("status", 200);
                    responseNode.put("operacao", "atualizarCompetenciaExperiencia");
                    responseNode.put("mensagem", "Competências e experiências atualizadas com sucesso.");
                }
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao atualizar competências e experiências: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }

        responseWriter.println(responseNode.toString());
    }








    private static void apagarCompetenciaExperiencia(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        String email = requestData.get("email").asText();
        String token = requestData.get("token").asText();
        JsonNode competenciasExperiencias = requestData.get("competenciaExperiencia");

        // Verificar se o token é válido
        if (!emailToSessionMap.containsKey(email) || !emailToSessionMap.get(email).equals(token)) {
            responseNode.put("status", 401);
            responseNode.put("mensagem", "Token de autenticação inválido");
            responseWriter.println(responseNode.toString());
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            Pessoa candidato = session.createQuery("FROM pessoa WHERE email = :email", Pessoa.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (candidato == null) {
                responseNode.put("status", 404);
                responseNode.put("mensagem", "Candidato não encontrado");
                responseWriter.println(responseNode.toString());
                return;
            }

            Transaction transaction = session.beginTransaction();
            try {
                for (JsonNode compExpNode : competenciasExperiencias) {
                    String competencia = compExpNode.get("competencia").asText();
                    CompetenciaExperiencia competenciaExperiencia = session.createQuery("FROM CompetenciaExperiencia WHERE candidato = :candidato AND competencia = :competencia", CompetenciaExperiencia.class)
                            .setParameter("candidato", candidato)
                            .setParameter("competencia", competencia)
                            .uniqueResult();

                    if (competenciaExperiencia != null) {
                        session.delete(competenciaExperiencia);
                    } else {
                        responseNode.put("status", 404);
                        responseNode.put("mensagem", "Competência não encontrada: " + competencia);
                        responseWriter.println(responseNode.toString());
                        transaction.rollback();
                        return;
                    }
                }

                transaction.commit();
                responseNode.put("status", 201);
                responseNode.put("operacao", "apagarCompetenciaExperiencia");
                responseNode.put("mensagem", "Competências deletadas com sucesso");
            } catch (Exception e) {
                transaction.rollback();
                responseNode.put("status", 500);
                responseNode.put("mensagem", "Erro ao deletar competências: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            responseNode.put("status", 500);
            responseNode.put("mensagem", "Erro ao conectar ao banco de dados");
            e.printStackTrace();
        }

        responseWriter.println(responseNode.toString());
    }



    private static int createEmpresa(String razaoSocial, String email, String cnpj, String senha, String descricao, String ramo) {
        Empresa empresa = new Empresa(razaoSocial,email,cnpj,senha,descricao,ramo);
        empresa.setRazaoSocial(razaoSocial);
        empresa.setEmail(email);
        empresa.setCnpj(cnpj);
        empresa.setSenha(senha);
        empresa.setDescricao(descricao);
        empresa.setRamo(ramo);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(empresa);
                transaction.commit();
                return 201;
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return 500;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }
    private static void loginEmpresa(JsonNode requestData, ObjectNode responseNode, PrintWriter responseWriter) {
        // Verifica se os campos necessários estão presentes no JSON
        if (!requestData.has("email") || !requestData.has("senha")) {
            responseNode.put("status", 400);
            responseNode.put("mensagem", "E-mail e senha são obrigatórios.");
            responseWriter.println(responseNode.toString());
            return;
        }

        String email = requestData.get("email").asText();
        String senha = requestData.get("senha").asText();

        // Procura a empresa pelo e-mail e senha fornecidos
        Empresa empresa = getEmpresaByEmailAndSenha(email, senha);
        if (empresa != null) {
            String token = UUID.randomUUID().toString();
            emailToSessionMapEmp.put(email, token);
            sessionToUserMapEmp.put(token, empresa);
            responseNode.put("operacao", "loginEmpresa");
            responseNode.put("status", 200);
            responseNode.put("token", token);
            responseNode.put("email", email); // Incluindo o email na resposta para evitar null pointer no cliente
        } else {
            responseNode.put("status", 401);
            responseNode.put("operacao", "loginEmpresa");
            responseNode.put("mensagem", "E-mail ou senha incorretos");
        }
        responseWriter.println(responseNode.toString());
    }

    private static Empresa getEmpresaByEmailAndSenha(String email, String senha) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM empresa WHERE email = :email AND senha = :senha", Empresa.class)
                    .setParameter("email", email)
                    .setParameter("senha", senha)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("Erro ao buscar a empresa: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Pessoa getUserByEmail(String email) {
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
