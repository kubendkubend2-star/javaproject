package com.televet.Services;

import com.televet.Exception.ResourceNotFoundException;
import com.televet.Models.ApiDtos;
import com.televet.Models.Consultation;
import com.televet.Models.Contact;
import com.televet.Models.Livestock;
import com.televet.Respository.ConsultationRepository;
import com.televet.Respository.ContactRepository;
import com.televet.Respository.LivestockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConsultationService {
    private final ConsultationRepository consultations;
    private final ContactRepository contacts;
    private final LivestockRepository livestock;
    private final PriorityService priorityService;

    public ConsultationService(ConsultationRepository consultations, ContactRepository contacts,
            LivestockRepository livestock, PriorityService priorityService) {
        this.consultations = consultations;
        this.contacts = contacts;
        this.livestock = livestock;
        this.priorityService = priorityService;
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.ConsultationView> find() {
        return consultations.findAllByOrderByCreatedAtDesc().stream().map(ApiDtos.ConsultationView::from).toList();
    }

    @Transactional(readOnly = true)
    public ApiDtos.ConsultationView get(Long id) { return ApiDtos.ConsultationView.from(require(id)); }

    public ApiDtos.ConsultationView create(ApiDtos.ConsultationRequest request) {
        Contact farmer = contacts.findById(request.farmerId())
                .filter(contact -> contact.getContactType() == Contact.ContactType.FARMER)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found: " + request.farmerId()));
        Livestock animal = livestock.findById(request.livestockId())
                .orElseThrow(() -> new ResourceNotFoundException("Livestock not found: " + request.livestockId()));
        if (!animal.getFarmer().getId().equals(farmer.getId())) throw new IllegalArgumentException("Selected livestock does not belong to this farmer");
        Consultation consultation = new Consultation();
        consultation.setConsultationNumber("TV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        consultation.setFarmer(farmer);
        consultation.setLivestock(animal);
        consultation.setSymptoms(request.symptoms().trim());
        consultation.setDiagnosticPriority(priorityService.calculate(request.symptoms()));
        consultation.setConsultationType(request.consultationType() == null ? Consultation.ConsultationType.CHAT : request.consultationType());
        consultation.setConsultationDate(request.consultationDate() == null ? LocalDateTime.now() : request.consultationDate());
        consultation.setConsultationFee(request.consultationFee() == null ? BigDecimal.ZERO : request.consultationFee());
        consultation.setClinicalNotes(request.clinicalNotes());
        if (request.veterinarianId() != null) consultation.setVeterinarian(findSpecialist(request.veterinarianId()));
        return ApiDtos.ConsultationView.from(consultations.save(consultation));
    }

    public ApiDtos.ConsultationView update(Long id, ApiDtos.ConsultationUpdate request) {
        Consultation consultation = require(id);
        if (request.symptoms() != null && !request.symptoms().isBlank()) {
            consultation.setSymptoms(request.symptoms().trim());
            consultation.setDiagnosticPriority(priorityService.calculate(request.symptoms()));
        }
        if (request.clinicalNotes() != null) consultation.setClinicalNotes(request.clinicalNotes());
        if (request.diagnosis() != null) consultation.setDiagnosis(request.diagnosis());
        if (request.status() != null) consultation.setStatus(request.status());
        if (request.consultationType() != null) consultation.setConsultationType(request.consultationType());
        if (request.consultationFee() != null) {
            if (request.consultationFee().signum() < 0) throw new IllegalArgumentException("Consultation fee cannot be negative");
            consultation.setConsultationFee(request.consultationFee());
        }
        if (request.followUpDate() != null) consultation.setFollowUpDate(request.followUpDate());
        return ApiDtos.ConsultationView.from(consultations.save(consultation));
    }

    public ApiDtos.ConsultationView assign(Long id, Long specialistId) {
        Consultation consultation = require(id);
        consultation.setVeterinarian(findSpecialist(specialistId));
        if (consultation.getStatus() == Consultation.Status.REQUESTED) consultation.setStatus(Consultation.Status.SCHEDULED);
        return ApiDtos.ConsultationView.from(consultations.save(consultation));
    }

    public ApiDtos.ConsultationView complete(Long id, ApiDtos.CompletionRequest request) {
        Consultation consultation = require(id);
        consultation.setDiagnosis(request.diagnosis());
        consultation.setClinicalNotes(request.clinicalNotes());
        consultation.setFollowUpDate(request.followUpDate());
        consultation.setStatus(Consultation.Status.COMPLETED);
        Livestock animal = consultation.getLivestock();
        animal.setHealthStatus(Livestock.HealthStatus.UNDER_TREATMENT);
        return ApiDtos.ConsultationView.from(consultations.save(consultation));
    }

    public void delete(Long id) { consultations.delete(require(id)); }

    private Consultation require(Long id) {
        return consultations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Consultation not found: " + id));
    }

    private Contact findSpecialist(Long id) {
        return contacts.findById(id).filter(contact -> contact.getContactType() == Contact.ContactType.VETERINARY_SPECIALIST)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinary specialist not found: " + id));
    }
}