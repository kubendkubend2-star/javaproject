package com.televet.Data;

import com.televet.Models.Consultation;
import com.televet.Models.Contact;
import com.televet.Models.Livestock;
import com.televet.Respository.ConsultationRepository;
import com.televet.Respository.ContactRepository;
import com.televet.Respository.LivestockRepository;
import com.televet.Services.PriorityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {
    private final ContactRepository contacts;
    private final LivestockRepository livestock;
    private final ConsultationRepository consultations;
    private final PriorityService priorityService;

    public SampleDataLoader(ContactRepository contacts, LivestockRepository livestock,
            ConsultationRepository consultations, PriorityService priorityService) {
        this.contacts = contacts;
        this.livestock = livestock;
        this.consultations = consultations;
        this.priorityService = priorityService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (contacts.count() > 0) return;

        String[] farmerNames = {"Ravi Kumar", "Meena Devi", "Arjun Patel", "Lakshmi Rao", "Suresh Yadav",
                "Anita Sharma", "Kiran Reddy", "Pooja Singh", "Mahesh Joshi", "Farida Khan"};
        List<Contact> farmers = new ArrayList<>();
        for (int index = 0; index < farmerNames.length; index++) {
            Contact farmer = new Contact();
            farmer.setContactCode(String.format("FAR-%04d", index + 1));
            farmer.setName(farmerNames[index]);
            farmer.setEmail("farmer" + (index + 1) + "@demo.local");
            farmer.setPhone("98765" + String.format("%05d", index + 1));
            farmer.setDistrict(new String[] {"Mysuru", "Mandya", "Hassan", "Tumakuru", "Shivamogga"}[index % 5]);
            farmer.setState("Karnataka");
            farmer.setFarmName(farmerNames[index].split(" ")[0] + " Farm");
            farmer.setContactType(Contact.ContactType.FARMER);
            farmers.add(contacts.save(farmer));
        }

        String[] specialistNames = {"Dr. Nisha Rao", "Dr. Vikram Shah", "Dr. Anil Menon", "Dr. Kavya Iyer", "Dr. Sameer Das"};
        String[] specialties = {"Large Animal Medicine", "Veterinary Surgery", "Veterinary Pathology", "Small Ruminant Medicine", "General Veterinary Medicine"};
        List<Contact> specialists = new ArrayList<>();
        for (int index = 0; index < specialistNames.length; index++) {
            Contact specialist = new Contact();
            specialist.setContactCode(String.format("VET-%04d", index + 1));
            specialist.setName(specialistNames[index]);
            specialist.setEmail("vet" + (index + 1) + "@demo.local");
            specialist.setPhone("91234" + String.format("%05d", index + 1));
            specialist.setSpecialization(specialties[index]);
            specialist.setQualification("BVSc & AH");
            specialist.setLicenseNumber("KA-VET-" + (1000 + index));
            specialist.setYearsOfExperience(6 + index * 2);
            specialist.setConsultationFee(new BigDecimal("500.00"));
            specialist.setContactType(Contact.ContactType.VETERINARY_SPECIALIST);
            specialists.add(contacts.save(specialist));
        }

        String[] vendorNames = {"GreenField Vet Supplies", "Rural Diagnostics Lab", "AgriCare Pharma", "MediVet Distributors", "BioTest Laboratories"};
        for (int index = 0; index < vendorNames.length; index++) {
            Contact vendor = new Contact();
            vendor.setContactCode(String.format("VEN-%04d", index + 1));
            vendor.setName(vendorNames[index]);
            vendor.setCompanyName(vendorNames[index]);
            vendor.setEmail("vendor" + (index + 1) + "@demo.local");
            vendor.setPhone("90123" + String.format("%05d", index + 1));
            vendor.setDistrict("Mysuru");
            vendor.setState("Karnataka");
            vendor.setVendorType(index % 2 == 0 ? "PHARMACEUTICAL" : "LABORATORY");
            vendor.setContactType(Contact.ContactType.PHARMA_LAB_VENDOR);
            contacts.save(vendor);
        }

        String[] species = {"Cattle", "Buffalo", "Goat", "Sheep", "Cattle"};
        String[] breeds = {"Hallikar", "Murrah", "Jamunapari", "Deccani", "Gir"};
        List<Livestock> herd = new ArrayList<>();
        for (int index = 0; index < 20; index++) {
            Livestock animal = new Livestock();
            animal.setTagNumber(String.format("TV-%03d", index + 1));
            animal.setAnimalName(new String[] {"Gauri", "Moti", "Chinni", "Raja", "Kaveri"}[index % 5]);
            animal.setSpecies(species[index % species.length]);
            animal.setBreed(breeds[index % breeds.length]);
            animal.setGender(index % 2 == 0 ? "Female" : "Male");
            animal.setAge(2 + index % 7);
            animal.setWeight(new BigDecimal(180 + index * 13));
            animal.setFarmer(farmers.get(index / 2));
            if (index == 2) animal.setHealthStatus(Livestock.HealthStatus.UNDER_TREATMENT);
            if (index == 8) animal.setHealthStatus(Livestock.HealthStatus.CRITICAL);
            herd.add(livestock.save(animal));
        }

        String[] symptoms = {"High fever, loss of appetite and weakness", "Mild fever and reduced activity", "Routine health check",
                "Persistent fever and sudden weight loss", "Mild diarrhea", "Unable to stand and severe breathing difficulty",
                "Loss of appetite", "Reduced activity", "Severe diarrhea", "Routine follow-up"};
        for (int index = 0; index < symptoms.length; index++) {
            Consultation consultation = new Consultation();
            consultation.setConsultationNumber(String.format("TV-%04d", index + 1));
            consultation.setFarmer(farmers.get(index));
            consultation.setLivestock(herd.get(index * 2));
            consultation.setVeterinarian(specialists.get(index % specialists.size()));
            consultation.setSymptoms(symptoms[index]);
            consultation.setDiagnosticPriority(priorityService.calculate(symptoms[index]));
            consultation.setConsultationType(Consultation.ConsultationType.values()[index % 3]);
            consultation.setConsultationFee(new BigDecimal("500.00"));
            consultation.setConsultationDate(LocalDateTime.now().minusDays(index));
            if (index < 3) {
                consultation.setStatus(Consultation.Status.COMPLETED);
                consultation.setDiagnosis(index == 0 ? "Possible bacterial infection" : "Clinical assessment completed");
                consultation.setClinicalNotes("Demo clinical review. Diagnostic follow-up as indicated.");
            } else if (index == 3) {
                consultation.setStatus(Consultation.Status.IN_PROGRESS);
            }
            consultations.save(consultation);
        }
    }
}