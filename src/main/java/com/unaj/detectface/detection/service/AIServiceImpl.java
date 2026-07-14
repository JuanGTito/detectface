package com.unaj.detectface.detection.service;

import org.springframework.stereotype.Service;

@Service
public class AIServiceImpl implements AIService {
    @Override
    public String detectEmotion(byte[] imageBytes) {
        return "NEUTRAL"; // Valor por defecto
    }

    @Override
    public boolean detectCellPhoneUse(byte[] imageBytes) {
        return false;
    }

    @Override
    public double getConfidence(byte[] imageBytes) {
        return 1.0;
    }
}
