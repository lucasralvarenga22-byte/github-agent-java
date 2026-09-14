package com.github.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.agent.service.GitHubService;
import com.github.agent.service.DataProcessorService;
import com.github.agent.service.UserInteractionService;

/**
 * Classe principal do agente GitHub
 * Orquestra a interação entre os serviços
 */
public class GitHubAgentApp {
    private static final Logger logger = LoggerFactory.getLogger(GitHubAgentApp.class);

    private final GitHubService githubService;
    private final DataProcessorService dataProcessorService;
    private final UserInteractionService userInteractionService;

    public GitHubAgentApp(String githubToken) throws Exception {
        this.githubService = new GitHubService(githubToken);
        this.dataProcessorService = new DataProcessorService();
        this.userInteractionService = new UserInteractionService();
    }

    /**
     * Inicia o agente e apresenta menu interativo
     */
    public void start() {
        logger.info("Iniciando GitHub Agent...");
        userInteractionService.displayWelcomeMessage();

        boolean running = true;
        while (running) {
            userInteractionService.displayMainMenu();
            int choice = userInteractionService.getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        handleListRepositories();
                        break;
                    case 2:
                        handleCreateIssue();
                        break;
                    case 3:
                        handleListIssues();
                        break;
                    case 4:
                        handleProcessRepositoryData();
                        break;
                    case 5:
                        handleAnalyzeRepository();
                        break;
                    case 6:
                        running = false;
                        userInteractionService.displayExitMessage();
                        break;
                    default:
                        userInteractionService.displayInvalidChoice();
                }
            } catch (Exception e) {
                logger.error("Erro ao executar operação", e);
                userInteractionService.displayError("Erro ao executar operação: " + e.getMessage());
            }
        }
    }

    /**
     * Lista repositórios do usuário autenticado
     */
    private void handleListRepositories() throws Exception {
        userInteractionService.displayMessage("Carregando repositórios...");
        var repositories = githubService.listUserRepositories();
        userInteractionService.displayRepositories(repositories);
    }

    /**
     * Cria uma nova issue
     */
    private void handleCreateIssue() throws Exception {
        String owner = userInteractionService.getUserInput("Proprietário do repositório: ");
        String repo = userInteractionService.getUserInput("Nome do repositório: ");
        String title = userInteractionService.getUserInput("Título da issue: ");
        String body = userInteractionService.getUserInput("Descrição da issue: ");

        githubService.createIssue(owner, repo, title, body);
        userInteractionService.displayMessage("✓ Issue criada com sucesso!");
    }

    /**
     * Lista issues de um repositório
     */
    private void handleListIssues() throws Exception {
        String owner = userInteractionService.getUserInput("Proprietário do repositório: ");
        String repo = userInteractionService.getUserInput("Nome do repositório: ");

        var issues = githubService.listIssues(owner, repo);
        userInteractionService.displayIssues(issues);
    }

    /**
     * Processa dados de um repositório
     */
    private void handleProcessRepositoryData() throws Exception {
        String owner = userInteractionService.getUserInput("Proprietário do repositório: ");
        String repo = userInteractionService.getUserInput("Nome do repositório: ");

        var repoData = githubService.getRepositoryData(owner, repo);
        var processedData = dataProcessorService.processData(repoData);
        userInteractionService.displayProcessedData(processedData);
    }

    /**
     * Analisa um repositório e gera estatísticas
     */
    private void handleAnalyzeRepository() throws Exception {
        String owner = userInteractionService.getUserInput("Proprietário do repositório: ");
        String repo = userInteractionService.getUserInput("Nome do repositório: ");

        var analysis = githubService.analyzeRepository(owner, repo);
        var report = dataProcessorService.generateReport(analysis);
        userInteractionService.displayReport(report);
    }

    /**
     * Método principal
     */
    public static void main(String[] args) {
        String githubToken = System.getenv("GITHUB_TOKEN");
        
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("Erro: GITHUB_TOKEN não configurada!");
            System.err.println("Configure a variável de ambiente GITHUB_TOKEN com seu token do GitHub");
            System.exit(1);
        }

        try {
            GitHubAgentApp app = new GitHubAgentApp(githubToken);
            app.start();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar aplicação: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
