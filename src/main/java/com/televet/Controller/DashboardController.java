package com.televet.Controller;

import com.televet.Models.Consultation;
import com.televet.Models.Contact;
import com.televet.Models.Livestock;
import com.televet.Respository.ConsultationRepository;
import com.televet.Respository.ContactRepository;
import com.televet.Respository.LivestockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ContactRepository contacts;
    private final LivestockRepository livestock;
    private final ConsultationRepository consultations;

    public DashboardController(ContactRepository contacts, LivestockRepository livestock, ConsultationRepository consultations) {
        this.contacts = contacts;
        this.livestock = livestock;
        this.consultations = consultations;
    }

    @GetMapping
    public Map<String, Long> stats() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("totalFarmers", contacts.countByContactType(Contact.ContactType.FARMER));
        result.put("totalLivestock", livestock.count());
        result.put("activeVeterinarians", contacts.countByContactTypeAndStatus(Contact.ContactType.VETERINARY_SPECIALIST, "ACTIVE"));
        result.put("pendingConsultations", consultations.countByStatusIn(java.util.List.of(Consultation.Status.REQUESTED, Consultation.Status.SCHEDULED)));
        result.put("criticalConsultations", consultations.countByDiagnosticPriority(Consultation.Priority.CRITICAL));
        result.put("completedConsultations", consultations.countByStatus(Consultation.Status.COMPLETED));
        result.put("criticalLivestock", livestock.countByHealthStatus(Livestock.HealthStatus.CRITICAL));
        return result;
    }
}