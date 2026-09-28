package com.televet.Services;

import com.televet.Models.Consultation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityServiceTest {
    private final PriorityService service = new PriorityService();

    @Test
    void assignsCriticalPriorityToEmergencySymptoms() {
        assertEquals(Consultation.Priority.CRITICAL, service.calculate("Unable to stand and severe breathing difficulty"));
    }

    @Test
    void assignsHighPriorityToPersistentFever() {
        assertEquals(Consultation.Priority.HIGH, service.calculate("Persistent fever with weakness"));
    }

    @Test
    void assignsMediumPriorityToMildSymptoms() {
        assertEquals(Consultation.Priority.MEDIUM, service.calculate("Mild fever and reduced activity"));
    }

    @Test
    void defaultsUnmatchedSymptomsToLowPriority() {
        assertEquals(Consultation.Priority.LOW, service.calculate("Routine check, no symptoms reported"));
    }
}