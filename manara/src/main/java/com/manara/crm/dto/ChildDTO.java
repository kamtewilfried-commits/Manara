package com.manara.crm.dto;

import java.time.LocalDate;

public class ChildDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;

    private LocalDate birthDate;

    // 🔗 Parent simplifié
    private Long parentId;
    private String parentFullName;

    // ⚠️ Données sensibles (à garder ou non selon ton besoin)
    private String allergies;
    private String medicalNotes;

    public ChildDTO() {}

    public ChildDTO(Long id, String firstName, String lastName, LocalDate birthDate,
                    Long parentId, String parentFullName,
                    String allergies, String medicalNotes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.parentId = parentId;
        this.parentFullName = parentFullName;
        this.allergies = allergies;
        this.medicalNotes = medicalNotes;
        updateFullName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getFullName() {
        return fullName;
    }

    private void updateFullName() {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        this.fullName = (first + " " + last).trim();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentFullName() {
        return parentFullName;
    }

    public void setParentFullName(String parentFullName) {
        this.parentFullName = parentFullName;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }
}