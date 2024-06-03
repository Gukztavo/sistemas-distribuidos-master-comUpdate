package network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import service.CompetenciaService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Cliente {
    protected static int defaultPort = 22222;
    private static String currentUserEmail;
    private static String currentEmpEmail;
    private static String currentToken;
    private static String currentTokenEmp;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String serverHostname = getInput(scanner, "Digite o IP do servidor (o padrão é 127.0.0.1): ", "127.0.0.1");
        int serverPort = getPort(scanner, "Digite a da porta do servidor (o padrão é 22222): ", defaultPort);

        System.out.println("Tentando conectar ao servidor " + serverHostname + " na porta " + serverPort + ".");
        try (
                Socket echoSocket = new Socket(serverHostname, serverPort);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Conectado ao servidor.");
            while (true) {
                System.out.println("Selecione a categoria:");
                System.out.println("1. Candidato");
                System.out.println("2. Empresa");
                System.out.println("3. Sair");
                System.out.print("Opção: ");
                String categoryChoice = stdIn.readLine();

                switch (categoryChoice) {
                    case "1":
                        handleUserOperations(stdIn, out, in);
                        break;
                    case "2":
                        operacoesEmpresa(stdIn, out, in);
                        break;
                    case "3":
                        System.out.println("Encerrando o cliente.");
                        return;
                    default:
                        System.out.println("Opção inválida. Por favor, tente novamente.");
                        break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Não foi possível encontrar o host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Não foi possível obter E/S para a conexão com: " + serverHostname);
            System.exit(1);
        }
    }

    private static String getInput(Scanner scanner, String message, String defaultValue) {
        System.out.print(message);
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }

    private static int getPort(Scanner scanner, String message, int defaultPort) {
        System.out.print(message);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Número de porta inválido. Usando a porta padrão " + defaultPort + ".");
            return defaultPort;
        }
    }

    private static void handleUserOperations(BufferedReader stdIn, PrintWriter out, BufferedReader in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        while (true) {
            System.out.println("Escolha a operação:");
            System.out.println("1. Cadastrar Candidato");
            System.out.println("2. Login");
            System.out.println("3. Visualizar Perfil");
            System.out.println("4. Atualizar Candidato");
            System.out.println("5. Apagar Usuário");
            System.out.println("6. Cadastrar Competência/Experiência");
            System.out.println("7. Vizualizar Competencias/Experiencia");
            System.out.println("8. Atualizar Competencias/Experiencia");
            System.out.println("9. Apagar Competencias/Experiencia");
            System.out.println("10. Logout");
            System.out.print("Opção: ");
            String operationChoice = stdIn.readLine();

            ObjectNode json = mapper.createObjectNode();
            String operationResponse;

            switch (operationChoice) {
                case "1":
                    collectAndSendCandidateDetails(json, "cadastrarCandidato", stdIn, out);
                    break;
                case "2":
                    collectAndSendLoginDetails(json, "loginCandidato", stdIn, out);
                    break;
                case "3":
                    sendProfileRequest(json, out);
                    break;
                case "4":
                    sendUpdateRequest(json, stdIn, out);
                    break;
                case "5":
                    sendDeleteRequest(json, stdIn, out);
                    break;
                case "6":
                    sendCompetenciaExperienciaRequest(json, stdIn, out);
                    break;
                case "7":
                    sendVisualizarCompetenciaExperienciaRequest(json, out);
                    break;
                case "8":
                    sendUpdateCompetenciaExperienciaRequest(json, stdIn, out);
                    break;
                case "9":
                    sendDeleteCompetenciaRequest(json, stdIn, out, in);
                    break;
                case "10":
                    sendLogoutRequest(json, out);
                    break;
                default:
                    System.out.println("Opção inválida. Encerrando o cliente.");
                    return;
            }

            operationResponse = in.readLine();
            System.out.println("Resposta do servidor: " + operationResponse);
            JsonNode responseNode = mapper.readTree(operationResponse);

            if (operationChoice.equals("2") && responseNode.path("status").asInt() == 200) {
                if (responseNode.has("email") && responseNode.has("token")) {
                    currentUserEmail = responseNode.get("email").asText();
                    currentToken = responseNode.get("token").asText();
                } else {
                    System.out.println("Resposta do servidor não contém email ou token");
                }
            } else if (operationChoice.equals("7")) {
                currentUserEmail = null;
                currentToken = null;
            }
        }
    }

    private static void operacoesEmpresa(BufferedReader stdIn, PrintWriter out, BufferedReader in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        while (true) {
            System.out.println("Escolha a operação:");
            System.out.println("1. Cadastrar Empresa");
            System.out.println("2. Login");
            System.out.println("3. Visualizar Perfil");
            System.out.println("4. Atualizar Empresa");
            System.out.println("5. Apagar Empresa");
            System.out.println("6. Logout");
            System.out.print("Opção: ");
            String operationChoice = stdIn.readLine();

            ObjectNode json = mapper.createObjectNode();
            String operationResponse;

            switch (operationChoice) {
                case "1":
                    collectAndSendEmpresaDetails(json, "cadastrarEmpresa", stdIn, out);
                    break;
                case "2":
                    collectAndSendLoginEmpresa(json, "loginEmpresa", stdIn, out);
                    break;
                case "3":
                    sendEmpresaProfileRequest(json, out);
                    break;
                case "4":
                    sendUpdateEmpresaRequest(json, stdIn, out);
                    break;
                case "5":
                    sendDeleteEmpresaRequest(json, stdIn, out);
                    break;
                case "6":
                    sendLogoutEmpRequest(json, out);
                    break;
                default:
                    System.out.println("Opção inválida. Encerrando o cliente.");
                    return;
            }

            operationResponse = in.readLine();
            System.out.println("Resposta do servidor: " + operationResponse);
            JsonNode responseNode = mapper.readTree(operationResponse);

            if (operationChoice.equals("2") && responseNode.path("status").asInt() == 200) {
                if (responseNode.has("token")) {
                    currentTokenEmp = responseNode.get("token").asText();
                } else {
                    System.out.println("Resposta do servidor não contém email ou token");
                }
            } else if (operationChoice.equals("6")) {
                currentEmpEmail = null;
                currentTokenEmp = null;
            }
        }
    }


    private static void sendCompetenciaExperienciaRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        if (currentUserEmail == null || currentToken == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }

        json.put("operacao", "cadastrarCompetenciaExperiencia");
        json.put("email", currentUserEmail);
        json.put("token", currentToken);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode competenciasExperiencias = mapper.createArrayNode();

        List<String> competenciasFixas = CompetenciaService.getCompetenciasFixas();
        System.out.println("Escolha uma competência entre as opções: " + competenciasFixas);

        System.out.println("Digite a competência:");
        String competencia = stdIn.readLine();
        if (!competenciasFixas.contains(competencia)) {
            System.out.println("Competência inválida. Tente novamente.");
            return;
        }

        System.out.println("Digite os anos de experiência:");
        int experiencia = Integer.parseInt(stdIn.readLine());

        ObjectNode competenciaExperiencia = mapper.createObjectNode();
        competenciaExperiencia.put("competencia", competencia);
        competenciaExperiencia.put("experiencia", experiencia);

        competenciasExperiencias.add(competenciaExperiencia);

        json.set("competenciaExperiencia", competenciasExperiencias);
        out.println(json.toString());
    }


    private static void sendVisualizarCompetenciaExperienciaRequest(ObjectNode json, PrintWriter out) {
        if (currentUserEmail == null || currentToken == null) {
            System.out.println("Você precisa fazer login primeiro.");
        }

        json.put("operacao", "visualizarCompetenciaExperiencia");
        json.put("email", currentUserEmail);
        json.put("token", currentToken);

        out.println(json.toString());

    }

    private static void sendUpdateCompetenciaExperienciaRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        if (currentUserEmail == null || currentToken == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }

        json.put("operacao", "atualizarCompetenciaExperiencia");
        json.put("email", currentUserEmail);
        json.put("token", currentToken);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode competenciasExperiencias = mapper.createArrayNode();

        List<String> competenciasFixas = CompetenciaService.getCompetenciasFixas();
        System.out.println("Escolha uma competência entre as opções: " + competenciasFixas);

        System.out.println("Digite a competência atual:");
        String competenciaAtual = stdIn.readLine();
        while (!competenciasFixas.contains(competenciaAtual)) {
            System.out.println("Competência inválida. Tente novamente:");
            competenciaAtual = stdIn.readLine();
        }

        System.out.println("Digite o novo nome da competência:");
        String novaCompetencia = stdIn.readLine();

        System.out.println("Digite os anos de experiência:");
        int experiencia = Integer.parseInt(stdIn.readLine());

        ObjectNode competenciaExperiencia = mapper.createObjectNode();
        competenciaExperiencia.put("competenciaAtual", competenciaAtual);
        competenciaExperiencia.put("novaCompetencia", novaCompetencia);
        competenciaExperiencia.put("experiencia", experiencia);

        competenciasExperiencias.add(competenciaExperiencia);
        json.set("competenciaExperiencia", competenciasExperiencias);

        out.println(json.toString());
    }
    private static void sendDeleteCompetenciaRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out, BufferedReader in) throws IOException {
        if (currentUserEmail == null || currentToken == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }

        json.put("operacao", "apagarCompetenciaExperiencia");
        json.put("email", currentUserEmail);
        json.put("token", currentToken);

        System.out.println("Digite a competência que deseja deletar:");
        String competencia = stdIn.readLine();
        json.put("competencia", competencia);

        out.println(json.toString());


    }

    private static void collectAndSendEmpresaDetails(ObjectNode json, String operation, BufferedReader stdIn, PrintWriter out) throws IOException {
        json.put("operacao", operation);
        System.out.println("Digite a razaoSocial da empresa:");
        json.put("razaoSocial", stdIn.readLine());
        System.out.println("Digite o email da empresa:");
        json.put("email", stdIn.readLine());
        System.out.println("Digite o cnpj da empresa :");
        json.put("cnpj", stdIn.readLine());
        System.out.println("Digite a senha da empresa :");
        json.put("senha", stdIn.readLine());
        System.out.println("Digite a descricao da empresa :");
        json.put("descricao", stdIn.readLine());
        System.out.println("Digite o ramo da empresa :");
        json.put("ramo", stdIn.readLine());
        out.println(json.toString());
    }

    private static void collectAndSendCandidateDetails(ObjectNode json, String operation, BufferedReader stdIn, PrintWriter out) throws IOException {
        json.put("operacao", operation);
        System.out.println("Digite o nome do candidato:");
        json.put("nome", stdIn.readLine());
        System.out.println("Digite o email do candidato:");
        json.put("email", stdIn.readLine());
        System.out.println("Digite a senha do candidato:");
        json.put("senha", stdIn.readLine());
        out.println(json.toString());
    }

    private static void collectAndSendLoginEmpresa(ObjectNode json, String operation, BufferedReader stdIn, PrintWriter out) throws IOException {
        json.put("operacao", operation);
        System.out.println("Digite o email:");
        String emailSave =  stdIn.readLine();
        json.put("email", emailSave);
        currentEmpEmail = emailSave;
        System.out.println("Digite a senha:");
        json.put("senha", stdIn.readLine());
        out.println(json.toString());
    }

    private static void collectAndSendLoginDetails(ObjectNode json, String operation, BufferedReader stdIn, PrintWriter out) throws IOException {
        json.put("operacao", operation);
        System.out.println("Digite o email:");
        json.put("email", stdIn.readLine());
        System.out.println("Digite a senha:");
        json.put("senha", stdIn.readLine());
        out.println(json.toString());
    }

    private static void sendProfileRequest(ObjectNode json, PrintWriter out) {
        if (currentUserEmail == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }
        json.put("operacao", "visualizarCandidato");
        json.put("email", currentUserEmail);  // Inclui o email atual do usuário
        out.println(json.toString());
    }


    private static void sendEmpresaProfileRequest(ObjectNode json, PrintWriter out) {
        if (currentEmpEmail == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }
        json.put("operacao", "visualizarEmpresa");
        json.put("email", currentEmpEmail);
        //json.put("token", currentToken);
        out.println(json.toString());
    }

    private static void sendLogoutRequest(ObjectNode json, PrintWriter out) {
        if (currentUserEmail == null && currentEmpEmail == null) {
            System.out.println("Você não está logado.");
            return;
        }
        json.put("operacao", "logout");
        json.put("token", currentToken);
        out.println(json.toString());
    }
    private static void sendLogoutEmpRequest(ObjectNode json, PrintWriter out) {
        if (currentEmpEmail == null) {
            System.out.println("Você não está logado.");
            return;
        }
        json.put("operacao", "logout");
        json.put("token", currentTokenEmp);
        out.println(json.toString());
    }

    private static void sendDeleteRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        System.out.println("Digite o e-mail do usuário que deseja deletar:");
        String emailToDelete = stdIn.readLine();
        if (emailToDelete.isEmpty()) {
            System.out.println("e-mail não pode estar vazio.");
            return;
        }
        json.put("operacao", "apagarCandidato");
        json.put("email", emailToDelete);
        out.println(json.toString());
    }

    private static void sendDeleteEmpresaRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        System.out.println("Digite o e-mail da Empresa que deseja deletar:");
        String emailToDelete = stdIn.readLine();
        if (emailToDelete.isEmpty()) {
            System.out.println("e-mail não pode estar vazio.");
            return;
        }
        json.put("operacao", "apagarEmpresa");
        json.put("token",currentTokenEmp);
        json.put("email", emailToDelete);
        out.println(json.toString());
    }

    private static void sendUpdateRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        if (currentUserEmail == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }

        System.out.println("Digite o novo nome do candidato:");
        String newName = stdIn.readLine();
        System.out.println("Digite senha:");
        String newSenha = stdIn.readLine();

        json.put("operacao", "atualizarCandidato");
        json.put("email", currentUserEmail);
        json.put("nome", newName);
        json.put("senha", newSenha);

        out.println(json.toString());
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String response = in.readLine();
        System.out.println("Resposta do servidor: " + response);
    }

    private static void sendUpdateEmpresaRequest(ObjectNode json, BufferedReader stdIn, PrintWriter out) throws IOException {
        if (currentEmpEmail == null) {
            System.out.println("Você precisa fazer login primeiro.");
            return;
        }

        System.out.println("Digite a nova razão social da empresa:");
        String newRazaoSocial = stdIn.readLine();
        System.out.println("Digite o email da empresa:");
        String newEmail = stdIn.readLine();
        System.out.println("Digite o novo CNPJ da empresa:");
        String newCnpj = stdIn.readLine();
        System.out.println("Digite a nova senha da empresa:");
        String newSenha = stdIn.readLine();
        System.out.println("Digite a nova descrição da empresa:");
        String newDescricao = stdIn.readLine();
        System.out.println("Digite o novo ramo da empresa:");
        String newRamo = stdIn.readLine();

        json.put("operacao", "atualizarEmpresa");
        json.put("emailAtual", currentEmpEmail); // Adiciona o email atual da empresa para identificar a sessão
        json.put("razaoSocial", newRazaoSocial);
        json.put("email",newEmail);
        json.put("cnpj", newCnpj);
        json.put("senha", newSenha);
        json.put("descricao", newDescricao);
        json.put("ramo", newRamo);

        out.println(json.toString());
    }


}
