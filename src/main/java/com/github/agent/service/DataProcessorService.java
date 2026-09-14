package com.github.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;

/**
 * Serviço para processamento e análise de dados
 * Transforma dados brutos em informações úteis e relatórios
 */
public class DataProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(DataProcessorService.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Processa dados brutos de um repositório
     */
    public Map<String, Object> processData(Map<String, Object> rawData) {
        logger.info("Processando dados do repositório");
        
        Map<String, Object> processed = new LinkedHashMap<>();
        
        // Informações básicas
        processed.put("repositoryName", rawData.get("name"));
        processed.put("repositoryUrl", rawData.get("url"));
        
        // Métricas de popularidade
        Map<String, Object> popularity = new LinkedHashMap<>();
        popularity.put("stars", rawData.get("stars"));
        popularity.put("forks", rawData.get("forks"));
        popularity.put("watchers", rawData.get("watchers"));
        processed.put("popularity", popularity);
        
        // Status do projeto
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("openIssues", rawData.get("openIssues"));
        status.put("language", rawData.get("language"));
        status.put("isPrivate", rawData.get("isPrivate"));
        status.put("createdAt", rawData.get("createdAt"));
        status.put("updatedAt", rawData.get("updatedAt"));
        processed.put("status", status);
        
        // Análise de saúde do projeto
        Map<String, Object> health = analyzeProjectHealth(rawData);
        processed.put("projectHealth", health);
        
        return processed;
    }

    /**
     * Analisa a saúde do projeto baseado em métricas
     */
    private Map<String, Object> analyzeProjectHealth(Map<String, Object> data) {
        Map<String, Object> health = new LinkedHashMap<>();
        
        int stars = ((Number) data.getOrDefault("stars", 0)).intValue();
        int forks = ((Number) data.getOrDefault("forks", 0)).intValue();
        int openIssues = ((Number) data.getOrDefault("openIssues", 0)).intValue();
        
        // Calcula score de saúde (0-100)
        int healthScore = calculateHealthScore(stars, forks, openIssues);
        health.put("healthScore", healthScore);
        health.put("status", getHealthStatus(healthScore));
        
        // Recomendações
        List<String> recommendations = generateRecommendations(stars, openIssues);
        health.put("recommendations", recommendations);
        
        return health;
    }

    /**
     * Calcula score de saúde do projeto
     */
    private int calculateHealthScore(int stars, int forks, int openIssues) {
        int score = 50; // Score base
        
        // Adiciona pontos por popularidade
        if (stars > 100) score += 20;
        else if (stars > 10) score += 10;
        
        if (forks > 50) score += 15;
        else if (forks > 5) score += 7;
        
        // Subtrai pontos por issues abertos
        if (openIssues > 100) score -= 20;
        else if (openIssues > 50) score -= 10;
        else if (openIssues > 10) score -= 5;
        
        return Math.max(0, Math.min(100, score));
    }

    /**
     * Retorna status de saúde baseado no score
     */
    private String getHealthStatus(int score) {
        if (score >= 80) return "Excelente";
        if (score >= 60) return "Bom";
        if (score >= 40) return "Aceitável";
        if (score >= 20) return "Precisa de atenção";
        return "Crítico";
    }

    /**
     * Gera recomendações baseadas em métricas
     */
    private List<String> generateRecommendations(int stars, int openIssues) {
        List<String> recommendations = new ArrayList<>();
        
        if (stars < 10) {
            recommendations.add("Promova o projeto para aumentar a visibilidade");
        }
        
        if (openIssues > 50) {
            recommendations.add("Revise e priorize as issues abertas");
            recommendations.add("Considere criar um roadmap público");
        }
        
        if (openIssues > 100) {
            recommendations.add("Muitas issues abertas - aumente a priorização");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Projeto em bom estado de manutenção!");
        }
        
        return recommendations;
    }

    /**
     * Gera relatório completo de análise
     */
    public String generateReport(Map<String, Object> analysis) {
        logger.info("Gerando relatório de análise");
        
        StringBuilder report = new StringBuilder();
        report.append("\n========================================\n");
        report.append("RELATÓRIO DE ANÁLISE DO REPOSITÓRIO\n");
        report.append("========================================\n\n");
        
        report.append("📊 INFORMAÇÕES BÁSICAS\n");
        report.append("  Nome: ").append(analysis.get("name")).append("\n");
        report.append("  Proprietário: ").append(analysis.get("owner")).append("\n");
        report.append("  Privado: ").append(analysis.get("isPrivate")).append("\n");
        report.append("  Arquivado: ").append(analysis.get("isArchived")).append("\n\n");
        
        report.append("⭐ ESTATÍSTICAS\n");
        report.append("  Stars: ").append(analysis.get("stargazers")).append("\n");
        report.append("  Forks: ").append(analysis.get("forks")).append("\n");
        report.append("  Watchers: ").append(analysis.get("watchers")).append("\n");
        report.append("  Issues Abertas: ").append(analysis.get("openIssues")).append("\n");
        report.append("  Branches: ").append(analysis.get("branches")).append("\n");
        report.append("  Releases: ").append(analysis.get("releases")).append("\n\n");
        
        report.append("💻 TECNOLOGIAS\n");
        report.append("  Linguagem Principal: ").append(analysis.get("language")).append("\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> languages = (Map<String, Integer>) analysis.get("languages");
        if (languages != null && !languages.isEmpty()) {
            report.append("  Linguagens Utilizadas:\n");
            languages.forEach((lang, count) -> 
                report.append("    - ").append(lang).append(": ").append(count).append(" bytes\n")
            );
        }
        
        report.append("\n========================================\n");
        
        return report.toString();
    }
}
