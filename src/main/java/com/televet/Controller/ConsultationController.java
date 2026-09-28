package com.televet.Controller;

import com.televet.Models.ApiDtos;
import com.televet.Services.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/televet/consultations")
public class ConsultationController {
    private final ConsultationService service;

    public ConsultationController(ConsultationService service) { this.service = service; }

    @GetMapping public List<ApiDtos.ConsultationView> list() { return service.find(); }
    @GetMapping("/{id}") public ApiDtos.ConsultationView get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ConsultationView create(@Valid @RequestBody ApiDtos.ConsultationRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public ApiDtos.ConsultationView update(@PathVariable Long id, @RequestBody ApiDtos.ConsultationUpdate request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/assign-specialist")
    public ApiDtos.ConsultationView assign(@PathVariable Long id, @Valid @RequestBody ApiDtos.AssignmentRequest request) { return service.assign(id, request.specialistId()); }
    @PostMapping("/{id}/complete")
    public ApiDtos.ConsultationView complete(@PathVariable Long id, @Valid @RequestBody ApiDtos.CompletionRequest request) { return service.complete(id, request); }
}