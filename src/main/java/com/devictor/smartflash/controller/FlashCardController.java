/**
 * @author victor.barbosa
 */

package com.devictor.smartflash.controller;

import com.devictor.smartflash.model.FlashCard;
import com.devictor.smartflash.service.AIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flashcards")
public class FlashCardController {

    private final AIService aiService;

    public FlashCardController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<List<FlashCard>> generate(@RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        List<FlashCard> flashCards = aiService.generateFlashCard(content);
        if (flashCards == null || flashCards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(flashCards);
    }
}
