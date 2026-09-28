package com.televet.Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "livestock", uniqueConstraints = @UniqueConstraint(columnNames = "tag_number"))
public class Livestock {
    public enum HealthStatus { HEALTHY, UNDER_TREATMENT, CRITICAL, RECOVERED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tag_number", nullable = false, unique = true)
    private String tagNumber;
    private String animalName;
    @Column(nullable = false)
    private String species;
    private String breed;
    private String gender;
    private Integer age;
    private BigDecimal weight;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Contact farmer;
    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;
    private LocalDate registrationDate = LocalDate.now();

    public Livestock() { }

    public Long getId() { return id; }
    public String getTagNumber() { return tagNumber; }
    public void setTagNumber(String tagNumber) { this.tagNumber = tagNumber; }
    public String getAnimalName() { return animalName; }
    public void setAnimalName(String animalName) { this.animalName = animalName; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Contact getFarmer() { return farmer; }
    public void setFarmer(Contact farmer) { this.farmer = farmer; }
    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}