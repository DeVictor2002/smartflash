/**
 * @author victor.barbosa
 */

package com.devictor.smartflash.service;

import com.devictor.smartflash.model.FlashCard;

import java.util.List;

public interface AIService {
    List<FlashCard> generateFlashCard(String content);
}
