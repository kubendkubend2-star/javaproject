package com.televet.Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", uniqueConstraints = @UniqueConstraint(columnNames = "contact_code"))
public class Contact {
    public enum ContactType { FARMER, VETERINARY_SPECIALIST, PHARMA_LAB_VENDOR }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contact_code", nullable = false, unique = true)
    private String contactCode;
    @Column(nullable = false)
    private String name;
    @Column(unique = true)
    private String email;
    private String phone;
    private String address;
    private String district;
    private String state;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType contactType;
    private String status = "ACTIVE";
    private String farmName;
    private Integer livestockCount;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private BigDecimal consultationFee;
    private String companyName;
    private String gstNumber;
    private String vendorType;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Contact() { }

    public Long getId() { return id; }
    public String getContactCode() { return contactCode; }
    public void setContactCode(String contactCode) { this.contactCode = contactCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public ContactType getContactType() { return contactType; }
    public void setContactType(ContactType contactType) { this.contactType = contactType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public Integer getLivestockCount() { return livestockCount; }
    public void setLivestockCount(Integer livestockCount) { this.livestockCount = livestockCount; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getVendorType() { return vendorType; }
    public void setVendorType(String vendorType) { this.vendorType = vendorType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}