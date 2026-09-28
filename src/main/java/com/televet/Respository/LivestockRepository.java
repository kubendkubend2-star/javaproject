package com.televet.Respository;

import com.televet.Models.Livestock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LivestockRepository extends JpaRepository<Livestock, Long> {
    List<Livestock> findAllByOrderByRegistrationDateDesc();
    @Query("select l from Livestock l where lower(l.tagNumber) like lower(concat('%', :query, '%')) or " +
            "lower(l.species) like lower(concat('%', :query, '%')) or " +
            "lower(coalesce(l.animalName, '')) like lower(concat('%', :query, '%')) or " +
            "lower(l.farmer.name) like lower(concat('%', :query, '%')) order by l.registrationDate desc")
    List<Livestock> search(@Param("query") String query);
    long countByHealthStatus(Livestock.HealthStatus status);
    boolean existsByTagNumber(String tagNumber);
}