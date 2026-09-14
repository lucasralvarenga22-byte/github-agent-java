package com.github.agent.service;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import java.util.*;

/**
 * Serviço para interação com o usuário
 * Gerencia entrada/saída e apresentação de dados
 */
public class UserInteractionService {
    private final Scanner scanner;

    public UserInteractionService() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Exibe mensagem de boas-vindas
     */
    public void displayWelcomeMessage() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   GitHub Agent - Interativo em Java    ║");
        System.out.println("║   Versão 1.0.0                         ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        System.out.println("Bem-vindo ao GitHub Agent!");
        System.out.println("Este agente permite interagir com GitHub e processar dados.\n");
    }

    /**
     * Exibe mensagem de saída
     */
    public void displayExitMessage() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   Obrigado por usar GitHub Agent!      ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    /**
     * Exibe menu principal
     */
    public void displayMainMenu() {
        System.out.println("\n┌─ MENU PRINCIPAL ─────────────────────┐");
        System.out.println("│ 1. Listar repositórios                 │");
        System.out.println("│ 2. Criar uma nova issue                │");
        System.out.println("│ 3. Listar issues de um repositório     │");
        System.out.println("│ 4. Processar dados do repositório      │");
        System.out.println("│ 5. Analisar repositório                │");
        System.out.println("│ 6. Sair                                │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Obtém escolha do usuário
     */
    public int getUserChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Obtém entrada de texto do usuário
     */
    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Exibe mensagem geral
     */
    public void displayMessage(String message) {
        System.out.println("\n➜ " + message);
    }

    /**
     * Exibe mensagem de erro
     */
    public void displayError(String message) {
        System.out.println("\n❌ ERRO: " + message);
    }

    /**
     * Exibe escolha inválida
     */
    public void displayInvalidChoice() {
        System.out.println("\n⚠️  Opção inválida! Tente novamente.");
    }

    /**
     * Exibe lista de repositórios
     */
    public void displayRepositories(List<GHRepository> repositories) {
        if (repositories.isEmpty()) {
            System.out.println("\n📦 Nenhum repositório encontrado.");
            return;
        }

        System.out.println("\n📦 SEUS REPOSITÓRIOS:");
        System.out.println("─────────────────────────────────────────");
        
        for (int i = 0; i < repositories.size(); i++) {
            GHRepository repo = repositories.get(i);
            try {
                System.out.printf("%d. %s\n", i + 1, repo.getFullName());
                System.out.printf("   ⭐ %d | 🔄 %d | 👀 %d\n", 
                    repo.getStargazersCount(), 
                    repo.getForksCount(),
                    repo.getWatchersCount());
                System.out.printf("   URL: %s\n\n", repo.getHtmlUrl());
            } catch (Exception e) {
                System.out.println("   Erro ao carregar detalhes\n");
            }
        }
    }

    /**
     * Exibe lista de issues
     */
    public void displayIssues(List<GHIssue> issues) {
        if (issues.isEmpty()) {
            System.out.println("\n✓ Nenhuma issue aberta encontrada.");
            return;
        }

        System.out.println("\n📋 ISSUES ABERTAS:");
        System.out.println("─────────────────────────────────────────");
        
        for (int i = 0; i < issues.size(); i++) {
            GHIssue issue = issues.get(i);
            try {
                System.out.printf("%d. #%d - %s\n", i + 1, issue.getNumber(), issue.getTitle());
                System.out.printf("   Autor: %s | Estado: %s\n", 
                    issue.getUser().getLogin(), 
                    issue.getState());
                System.out.printf("   URL: %s\n\n", issue.getHtmlUrl());
            } catch (Exception e) {
                System.out.println("   Erro ao carregar detalhes\n");
            }
        }
    }

    /**
     * Exibe dados processados
     */
    public void displayProcessedData(Map<String, Object> data) {
        System.out.println("\n📊 DADOS PROCESSADOS:");
        System.out.println("─────────────────────────────────────────");
        
        displayMapRecursive(data, 0);
    }

    /**
     * Exibe mapa de forma recursiva com indentação
     */
    private void displayMapRecursive(Map<String, Object> map, int indent) {
        String indentation = "  ".repeat(indent);
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                System.out.println(indentation + "├─ " + entry.getKey() + ":");
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                displayMapRecursive(subMap, indent + 1);
            } else if (value instanceof List) {
                System.out.println(indentation + "├─ " + entry.getKey() + ":");
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) value;
                for (String item : list) {
                    System.out.println(indentation + "  ├─ " + item);
                }
            } else {
                System.out.println(indentation + "├─ " + entry.getKey() + ": " + value);
            }
        }
    }

    /**
     * Exibe relatório
     */
    public void displayReport(String report) {
        System.out.println(report);
    }
}
