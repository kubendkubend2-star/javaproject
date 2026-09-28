package com.televet.Controller;

import com.televet.Models.ApiDtos;
import com.televet.Models.Contact;
import com.televet.Services.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContactController {
    private final ContactService service;

    public ContactController(ContactService service) { this.service = service; }

    @GetMapping("/farmers")
    public List<ApiDtos.ContactView> farmers(@RequestParam(required = false) String q) { return service.find(Contact.ContactType.FARMER, q); }
    @GetMapping("/farmers/{id}")
    public ApiDtos.ContactView farmer(@PathVariable Long id) { return service.get(id, Contact.ContactType.FARMER); }
    @PostMapping("/farmers") @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ContactView createFarmer(@Valid @RequestBody ApiDtos.ContactRequest request) { return service.create(Contact.ContactType.FARMER, request); }
    @PutMapping("/farmers/{id}")
    public ApiDtos.ContactView updateFarmer(@PathVariable Long id, @Valid @RequestBody ApiDtos.ContactRequest request) { return service.update(id, Contact.ContactType.FARMER, request); }
    @DeleteMapping("/farmers/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFarmer(@PathVariable Long id) { service.delete(id, Contact.ContactType.FARMER); }

    @GetMapping("/specialists")
    public List<ApiDtos.ContactView> specialists(@RequestParam(required = false) String q) { return service.find(Contact.ContactType.VETERINARY_SPECIALIST, q); }
    @PostMapping("/specialists") @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ContactView createSpecialist(@Valid @RequestBody ApiDtos.ContactRequest request) { return service.create(Contact.ContactType.VETERINARY_SPECIALIST, request); }
    @PutMapping("/specialists/{id}")
    public ApiDtos.ContactView updateSpecialist(@PathVariable Long id, @Valid @RequestBody ApiDtos.ContactRequest request) { return service.update(id, Contact.ContactType.VETERINARY_SPECIALIST, request); }
    @DeleteMapping("/specialists/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialist(@PathVariable Long id) { service.delete(id, Contact.ContactType.VETERINARY_SPECIALIST); }

    @GetMapping("/vendors")
    public List<ApiDtos.ContactView> vendors(@RequestParam(required = false) String q) { return service.find(Contact.ContactType.PHARMA_LAB_VENDOR, q); }
    @PostMapping("/vendors") @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ContactView createVendor(@Valid @RequestBody ApiDtos.ContactRequest request) { return service.create(Contact.ContactType.PHARMA_LAB_VENDOR, request); }
    @PutMapping("/vendors/{id}")
    public ApiDtos.ContactView updateVendor(@PathVariable Long id, @Valid @RequestBody ApiDtos.ContactRequest request) { return service.update(id, Contact.ContactType.PHARMA_LAB_VENDOR, request); }
    @DeleteMapping("/vendors/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVendor(@PathVariable Long id) { service.delete(id, Contact.ContactType.PHARMA_LAB_VENDOR); }
}