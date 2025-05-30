package com.devictor.smartflash.service;

import com.devictor.smartflash.client.GroqClient;
import com.devictor.smartflash.model.FlashCard;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService implements AIService {

    private static final String MODEL = "llama3-8b-8192";

    private static final String SYSTEM_PROMPT = "Você é um gerador de flashcards.";

    private final GroqClient groqClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api.token}")
    private String groqApiToken;

    public GroqService(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    /**
     * Gera flashcards a partir do conteúdo informado, chamando a API Groq.
     *
     * @param content conteúdo base para gerar flashcards
     * @return lista de flashcards extraídos da resposta
     */
    @Override
    public List<FlashCard> generateFlashCard(String content) {
        Map<String, Object> request = buildRequest(content);

        try {
            // chama API e recebe resposta JSON como String
            String responseJson = groqClient.generateChatCompletion(groqApiToken, request);

            // extrai o campo "content" da resposta da API, que contém a string JSON com os flashcards
            String flashcardsJson = extractFlashcardsJsonFromResponse(responseJson);

            // desserializa o JSON dos flashcards para uma lista de FlashCard
            return objectMapper.readValue(flashcardsJson, new TypeReference<List<FlashCard>>() {
            });

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar flashcards usando Groq API", e);
        }
    }

    /**
     * Monta o payload para enviar para a API Groq.
     */
    private Map<String, Object> buildRequest(String content) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", MODEL);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", """
                        Gere 10 flashcards em português do Brasil com base no conteúdo a seguir e responda APENAS com uma estrutura JSON como esta:
                        {
                          "flashcards": [
                            { "question": "Pergunta exemplo?", "answer": "Resposta exemplo." }
                          ]
                        }
                        Conteúdo: """ + content)
        );

        request.put("messages", messages);
        return request;
    }

    /**
     * Extrai o JSON de flashcards da string de resposta da API.
     * A resposta da API tem uma estrutura com o campo "choices[0].message.content" que é uma string JSON.
     * Esta string JSON contém o objeto {"flashcards":[...]}.
     * Este método extrai só o valor do array flashcards como JSON String.
     */
    private String extractFlashcardsJsonFromResponse(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

        if (contentNode.isMissingNode() || contentNode.isNull() || !contentNode.isTextual() || contentNode.asText().isBlank()) {
            throw new RuntimeException("Resposta da API não contém o campo esperado 'choices[0].message.content'");
        }

        String content = contentNode.asText();
        JsonNode contentJsonNode = objectMapper.readTree(content);
        JsonNode flashcardsNode = contentJsonNode.path("flashcards");

        if (!flashcardsNode.isArray()) {
            throw new RuntimeException("Campo 'flashcards' não é um array na resposta");
        }

        return flashcardsNode.toString();
    }
}
