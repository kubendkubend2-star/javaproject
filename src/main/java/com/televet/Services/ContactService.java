package com.televet.Services;

import com.televet.Exception.ResourceNotFoundException;
import com.televet.Models.ApiDtos;
import com.televet.Models.Contact;
import com.televet.Respository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContactService {
    private final ContactRepository repository;

    public ContactService(ContactRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<ApiDtos.ContactView> find(Contact.ContactType type, String query) {
        List<Contact> contacts = query == null || query.isBlank()
                ? repository.findByContactTypeOrderByName(type)
            : repository.searchByType(type, query.trim());
        return contacts.stream().map(ApiDtos.ContactView::from).toList();
    }

    @Transactional(readOnly = true)
    public ApiDtos.ContactView get(Long id, Contact.ContactType type) {
        return ApiDtos.ContactView.from(require(id, type));
    }

    public ApiDtos.ContactView create(Contact.ContactType type, ApiDtos.ContactRequest request) {
        Contact contact = new Contact();
        contact.setContactType(type);
        apply(contact, request);
        String prefix = switch (type) {
            case FARMER -> "FAR";
            case VETERINARY_SPECIALIST -> "VET";
            case PHARMA_LAB_VENDOR -> "VEN";
        };
        contact.setContactCode(request.contactCode() == null || request.contactCode().isBlank()
                ? prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : request.contactCode());
        return ApiDtos.ContactView.from(repository.save(contact));
    }

    public ApiDtos.ContactView update(Long id, Contact.ContactType type, ApiDtos.ContactRequest request) {
        Contact contact = require(id, type);
        apply(contact, request);
        if (request.contactCode() != null && !request.contactCode().isBlank()) contact.setContactCode(request.contactCode());
        return ApiDtos.ContactView.from(repository.save(contact));
    }

    public void delete(Long id, Contact.ContactType type) { repository.delete(require(id, type)); }

    private Contact require(Long id, Contact.ContactType type) {
        Contact contact = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
        if (contact.getContactType() != type) throw new ResourceNotFoundException("Contact not found: " + id);
        return contact;
    }

    private void apply(Contact contact, ApiDtos.ContactRequest request) {
        contact.setName(request.name());
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setAddress(request.address());
        contact.setDistrict(request.district());
        contact.setState(request.state());
        contact.setFarmName(request.farmName());
        contact.setLivestockCount(request.livestockCount());
        contact.setSpecialization(request.specialization());
        contact.setQualification(request.qualification());
        contact.setLicenseNumber(request.licenseNumber());
        contact.setYearsOfExperience(request.yearsOfExperience());
        contact.setConsultationFee(request.consultationFee());
        contact.setCompanyName(request.companyName());
        contact.setGstNumber(request.gstNumber());
        contact.setVendorType(request.vendorType());
        if (request.status() != null && !request.status().isBlank()) contact.setStatus(request.status());
    }
}