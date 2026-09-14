package com.github.agent.service;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

/**
 * Serviço de integração com GitHub API
 * Gerencia todas as operações com repositórios, issues e dados
 */
public class GitHubService {
    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);
    private final GitHub github;

    public GitHubService(String token) throws IOException {
        this.github = new GitHubBuilder()
                .withOAuthToken(token)
                .build();
        logger.info("Autenticado com GitHub API");
    }

    /**
     * Lista repositórios do usuário autenticado
     */
    public List<GHRepository> listUserRepositories() throws IOException {
        logger.info("Listando repositórios do usuário");
        List<GHRepository> repos = new ArrayList<>();
        
        github.getMyself().listRepositories(10, GHRepository.Sort.UPDATED)
                .iterator()
                .forEachRemaining(repos::add);
        
        logger.info("Encontrados {} repositórios", repos.size());
        return repos;
    }

    /**
     * Cria uma nova issue em um repositório
     */
    public void createIssue(String owner, String repo, String title, String body) throws IOException {
        logger.info("Criando issue no repositório {}/{}", owner, repo);
        
        GHRepository repository = github.getRepository(owner + "/" + repo);
        repository.createIssue()
                .title(title)
                .body(body)
                .create();
        
        logger.info("Issue criada com sucesso");
    }

    /**
     * Lista issues de um repositório
     */
    public List<GHIssue> listIssues(String owner, String repo) throws IOException {
        logger.info("Listando issues de {}/{}", owner, repo);
        
        GHRepository repository = github.getRepository(owner + "/" + repo);
        List<GHIssue> issues = new ArrayList<>();
        
        repository.getIssues(GHIssueState.OPEN)
                .iterator()
                .forEachRemaining(issues::add);
        
        logger.info("Encontradas {} issues abertas", issues.size());
        return issues;
    }

    /**
     * Obtém dados de um repositório para processamento
     */
    public Map<String, Object> getRepositoryData(String owner, String repo) throws IOException {
        logger.info("Obtendo dados do repositório {}/{}", owner, repo);
        
        GHRepository repository = github.getRepository(owner + "/" + repo);
        Map<String, Object> data = new LinkedHashMap<>();
        
        data.put("name", repository.getName());
        data.put("description", repository.getDescription());
        data.put("url", repository.getHtmlUrl());
        data.put("stars", repository.getStargazersCount());
        data.put("forks", repository.getForksCount());
        data.put("watchers", repository.getWatchersCount());
        data.put("openIssues", repository.getOpenIssueCount());
        data.put("language", repository.getLanguage());
        data.put("isPrivate", repository.isPrivate());
        data.put("createdAt", repository.getCreatedAt());
        data.put("updatedAt", repository.getUpdatedAt());
        
        return data;
    }

    /**
     * Analisa um repositório e coleta estatísticas avançadas
     */
    public Map<String, Object> analyzeRepository(String owner, String repo) throws IOException {
        logger.info("Analisando repositório {}/{}", owner, repo);
        
        GHRepository repository = github.getRepository(owner + "/" + repo);
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        // Informações básicas
        analysis.put("name", repository.getName());
        analysis.put("owner", repository.getOwnerLogin());
        
        // Estatísticas
        analysis.put("stargazers", repository.getStargazersCount());
        analysis.put("forks", repository.getForksCount());
        analysis.put("watchers", repository.getWatchersCount());
        analysis.put("openIssues", repository.getOpenIssueCount());
        
        // Branches
        int branchCount = 0;
        for (@SuppressWarnings("unused") GHBranch branch : repository.getBranches().values()) {
            branchCount++;
        }
        analysis.put("branches", branchCount);
        
        // Releases
        int releaseCount = repository.listReleases().toList().size();
        analysis.put("releases", releaseCount);
        
        // Linguagens
        Map<String, Integer> languages = repository.getLanguages();
        analysis.put("languages", languages);
        
        // Detalhes
        analysis.put("isPrivate", repository.isPrivate());
        analysis.put("isArchived", repository.isArchived());
        analysis.put("hasWiki", repository.hasWiki());
        analysis.put("hasIssues", repository.hasIssues());
        analysis.put("language", repository.getLanguage());
        
        logger.info("Análise concluída");
        return analysis;
    }
}
