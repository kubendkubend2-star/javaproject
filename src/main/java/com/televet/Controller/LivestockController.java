package com.televet.Controller;

import com.televet.Models.ApiDtos;
import com.televet.Services.LivestockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livestock")
public class LivestockController {
    private final LivestockService service;

    public LivestockController(LivestockService service) { this.service = service; }

    @GetMapping public List<ApiDtos.LivestockView> list(@RequestParam(required = false) String q) { return service.find(q); }
    @GetMapping("/{id}") public ApiDtos.LivestockView get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.LivestockView create(@Valid @RequestBody ApiDtos.LivestockRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public ApiDtos.LivestockView update(@PathVariable Long id, @Valid @RequestBody ApiDtos.LivestockRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}