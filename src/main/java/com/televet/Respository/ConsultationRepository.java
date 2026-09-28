package com.televet.Respository;

import com.televet.Models.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findAllByOrderByCreatedAtDesc();
    long countByStatusIn(List<Consultation.Status> statuses);
    long countByDiagnosticPriority(Consultation.Priority priority);
    long countByStatus(Consultation.Status status);
}