package com.televet.Services;

import com.televet.Models.Consultation;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class PriorityService {
    public Consultation.Priority calculate(String symptoms) {
        String text = symptoms.toLowerCase(Locale.ROOT);
        if (matches(text, "severe bleeding", "unable to stand", "severe breathing difficulty", "unconscious", "very high fever")) {
            return Consultation.Priority.CRITICAL;
        }
        if (matches(text, "persistent fever", "severe diarrhea", "loss of appetite", "sudden weight loss", "high fever")) {
            return Consultation.Priority.HIGH;
        }
        if (matches(text, "mild fever", "reduced activity", "mild diarrhea", "weakness")) {
            return Consultation.Priority.MEDIUM;
        }
        return Consultation.Priority.LOW;
    }

    private boolean matches(String symptoms, String... terms) {
        for (String term : terms) if (symptoms.contains(term)) return true;
        return false;
    }
}