package com.televet.Services;

import com.televet.Exception.ResourceNotFoundException;
import com.televet.Models.ApiDtos;
import com.televet.Models.Contact;
import com.televet.Models.Livestock;
import com.televet.Respository.ContactRepository;
import com.televet.Respository.LivestockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class LivestockService {
    private final LivestockRepository livestockRepository;
    private final ContactRepository contactRepository;

    public LivestockService(LivestockRepository livestockRepository, ContactRepository contactRepository) {
        this.livestockRepository = livestockRepository;
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.LivestockView> find(String query) {
        List<Livestock> animals = query == null || query.isBlank() ? livestockRepository.findAllByOrderByRegistrationDateDesc()
            : livestockRepository.search(query.trim());
        return animals.stream().map(ApiDtos.LivestockView::from).toList();
    }

    @Transactional(readOnly = true)
    public ApiDtos.LivestockView get(Long id) { return ApiDtos.LivestockView.from(require(id)); }

    public ApiDtos.LivestockView create(ApiDtos.LivestockRequest request) {
        Livestock animal = new Livestock();
        apply(animal, request);
        return ApiDtos.LivestockView.from(livestockRepository.save(animal));
    }

    public ApiDtos.LivestockView update(Long id, ApiDtos.LivestockRequest request) {
        Livestock animal = require(id);
        apply(animal, request);
        return ApiDtos.LivestockView.from(livestockRepository.save(animal));
    }

    public void delete(Long id) { livestockRepository.delete(require(id)); }

    private Livestock require(Long id) {
        return livestockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livestock not found: " + id));
    }

    private void apply(Livestock animal, ApiDtos.LivestockRequest request) {
        Contact farmer = contactRepository.findById(request.farmerId())
                .filter(contact -> contact.getContactType() == Contact.ContactType.FARMER)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found: " + request.farmerId()));
        animal.setTagNumber(request.tagNumber());
        animal.setAnimalName(request.animalName());
        animal.setSpecies(request.species());
        animal.setBreed(request.breed());
        animal.setGender(request.gender());
        animal.setAge(request.age());
        animal.setWeight(request.weight());
        animal.setFarmer(farmer);
        if (request.healthStatus() != null) animal.setHealthStatus(request.healthStatus());
        if (request.registrationDate() != null) animal.setRegistrationDate(request.registrationDate());
    }
}