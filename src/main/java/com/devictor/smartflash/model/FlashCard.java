/**
 * @author victor.barbosa
 */

package com.devictor.smartflash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class FlashCard {

    @JsonProperty("question")
    public String question;

    @JsonProperty("answer")
    public String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<FlashCard> extractFlashcardsFromApiResponse(String apiResponseJson) throws Exception {
        if (apiResponseJson == null) {
            throw new IllegalArgumentException("argument 'content' is null");
        }
        ObjectMapper mapper = new ObjectMapper();

        // Parse do JSON da resposta da API
        JsonNode root = mapper.readTree(apiResponseJson);

        String content = root.path("choices").get(0).path("message").path("content").asText();

        int start = content.indexOf('[');
        int end = content.lastIndexOf(']');

        if (start == -1 || end == -1 || start > end) {
            throw new RuntimeException("JSON sem array de flashcards");
        }

        String flashcardsJson = content.substring(start, end + 1);

        // Agora converte para lista de Flashcard
        List<FlashCard> flashcards = mapper.readValue(
                flashcardsJson,
                mapper.getTypeFactory().constructCollectionType(List.class, FlashCard.class)
        );
        return flashcards;
    }
}
