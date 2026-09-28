package com.televet.Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations", uniqueConstraints = @UniqueConstraint(columnNames = "consultation_number"))
public class Consultation {
    public enum ConsultationType { VIDEO, AUDIO, CHAT }
    public enum Status { REQUESTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "consultation_number", nullable = false, unique = true)
    private String consultationNumber;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Contact farmer;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Livestock livestock;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact veterinarian;
    private LocalDateTime consultationDate;
    @Column(nullable = false, length = 3000)
    private String symptoms;
    @Column(length = 3000)
    private String clinicalNotes;
    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType = ConsultationType.CHAT;
    @Enumerated(EnumType.STRING)
    private Status status = Status.REQUESTED;
    @Enumerated(EnumType.STRING)
    private Priority diagnosticPriority = Priority.LOW;
    private BigDecimal consultationFee = BigDecimal.ZERO;
    private String diagnosis;
    private LocalDate followUpDate;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Consultation() { }

    public Long getId() { return id; }
    public String getConsultationNumber() { return consultationNumber; }
    public void setConsultationNumber(String consultationNumber) { this.consultationNumber = consultationNumber; }
    public Contact getFarmer() { return farmer; }
    public void setFarmer(Contact farmer) { this.farmer = farmer; }
    public Livestock getLivestock() { return livestock; }
    public void setLivestock(Livestock livestock) { this.livestock = livestock; }
    public Contact getVeterinarian() { return veterinarian; }
    public void setVeterinarian(Contact veterinarian) { this.veterinarian = veterinarian; }
    public LocalDateTime getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDateTime consultationDate) { this.consultationDate = consultationDate; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }
    public ConsultationType getConsultationType() { return consultationType; }
    public void setConsultationType(ConsultationType consultationType) { this.consultationType = consultationType; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Priority getDiagnosticPriority() { return diagnosticPriority; }
    public void setDiagnosticPriority(Priority diagnosticPriority) { this.diagnosticPriority = diagnosticPriority; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}