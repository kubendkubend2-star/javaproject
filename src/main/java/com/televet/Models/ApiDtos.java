package com.televet.Models;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ApiDtos {
    private ApiDtos() { }

    public record ContactRequest(
            String contactCode,
            @NotBlank(message = "Name is required") String name,
            @Email(message = "Email must be valid") String email,
            @NotBlank(message = "Phone is required") String phone,
            String address,
            String district,
            String state,
            String farmName,
            Integer livestockCount,
            String specialization,
            String qualification,
            String licenseNumber,
            Integer yearsOfExperience,
            @DecimalMin(value = "0.0", message = "Consultation fee cannot be negative") BigDecimal consultationFee,
            String companyName,
            String gstNumber,
            String vendorType,
            String status) { }

    public record ContactView(Long id, String contactCode, String name, String email, String phone,
            String address, String district, String state, Contact.ContactType contactType,
            String status, String farmName, Integer livestockCount, String specialization,
            String qualification, String licenseNumber, Integer yearsOfExperience,
            BigDecimal consultationFee, String companyName, String gstNumber, String vendorType,
            LocalDateTime createdAt) {
        public static ContactView from(Contact contact) {
            return new ContactView(contact.getId(), contact.getContactCode(), contact.getName(), contact.getEmail(),
                    contact.getPhone(), contact.getAddress(), contact.getDistrict(), contact.getState(),
                    contact.getContactType(), contact.getStatus(), contact.getFarmName(), contact.getLivestockCount(),
                    contact.getSpecialization(), contact.getQualification(), contact.getLicenseNumber(),
                    contact.getYearsOfExperience(), contact.getConsultationFee(), contact.getCompanyName(),
                    contact.getGstNumber(), contact.getVendorType(), contact.getCreatedAt());
        }
    }

    public record LivestockRequest(
            @NotBlank(message = "Tag number is required") String tagNumber,
            String animalName,
            @NotBlank(message = "Species is required") String species,
            String breed,
            String gender,
            @Min(value = 0, message = "Age cannot be negative") Integer age,
            @DecimalMin(value = "0.0", message = "Weight cannot be negative") BigDecimal weight,
            @NotNull(message = "Farmer is required") Long farmerId,
            Livestock.HealthStatus healthStatus,
            LocalDate registrationDate) { }

    public record LivestockView(Long id, String tagNumber, String animalName, String species,
            String breed, String gender, Integer age, BigDecimal weight, Long farmerId,
            String farmerName, Livestock.HealthStatus healthStatus, LocalDate registrationDate) {
        public static LivestockView from(Livestock animal) {
            return new LivestockView(animal.getId(), animal.getTagNumber(), animal.getAnimalName(), animal.getSpecies(),
                    animal.getBreed(), animal.getGender(), animal.getAge(), animal.getWeight(), animal.getFarmer().getId(),
                    animal.getFarmer().getName(), animal.getHealthStatus(), animal.getRegistrationDate());
        }
    }

    public record ConsultationRequest(
            @NotNull(message = "Farmer is required") Long farmerId,
            @NotNull(message = "Livestock is required") Long livestockId,
            Long veterinarianId,
            @NotBlank(message = "Symptoms are required") String symptoms,
            String clinicalNotes,
            Consultation.ConsultationType consultationType,
            BigDecimal consultationFee,
            LocalDateTime consultationDate) { }

    public record ConsultationUpdate(String symptoms, String clinicalNotes, String diagnosis,
            Consultation.Status status, Consultation.ConsultationType consultationType,
            BigDecimal consultationFee, LocalDate followUpDate) { }

    public record AssignmentRequest(@NotNull(message = "Specialist is required") Long specialistId) { }

    public record CompletionRequest(@NotBlank(message = "Diagnosis is required") String diagnosis,
            String clinicalNotes, LocalDate followUpDate) { }

    public record ConsultationView(Long id, String consultationNumber, Long farmerId, String farmerName,
            Long livestockId, String livestockTag, String species, Long veterinarianId, String veterinarianName,
            LocalDateTime consultationDate, String symptoms, String clinicalNotes,
            Consultation.ConsultationType consultationType, Consultation.Status status,
            Consultation.Priority diagnosticPriority, BigDecimal consultationFee, String diagnosis,
            LocalDate followUpDate, LocalDateTime createdAt) {
        public static ConsultationView from(Consultation consultation) {
            Contact vet = consultation.getVeterinarian();
            return new ConsultationView(consultation.getId(), consultation.getConsultationNumber(),
                    consultation.getFarmer().getId(), consultation.getFarmer().getName(),
                    consultation.getLivestock().getId(), consultation.getLivestock().getTagNumber(),
                    consultation.getLivestock().getSpecies(), vet == null ? null : vet.getId(),
                    vet == null ? null : vet.getName(), consultation.getConsultationDate(), consultation.getSymptoms(),
                    consultation.getClinicalNotes(), consultation.getConsultationType(), consultation.getStatus(),
                    consultation.getDiagnosticPriority(), consultation.getConsultationFee(), consultation.getDiagnosis(),
                    consultation.getFollowUpDate(), consultation.getCreatedAt());
        }
    }
}