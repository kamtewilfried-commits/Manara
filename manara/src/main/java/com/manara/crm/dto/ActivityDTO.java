package com.manara.crm.dto;

import java.util.List;

public class ActivityDTO {

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private String categorie;
    private String photo;


    private double prixHoraire;

    private Long animateurId;
    private String animateurFullName;
    private String animateurEmail;
    private List<SessionDTO> sessions;
    private Integer sessionCount;

    public ActivityDTO() {
    }

    public ActivityDTO(Long id, String name, String description, boolean active, String categorie,
                       double prixHoraire,
                       Long animateurId, String animateurFullName, String animateurEmail,
                       Integer sessionCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.categorie = categorie;
        this.prixHoraire = prixHoraire;
        this.animateurId = animateurId;
        this.animateurFullName = animateurFullName;
        this.animateurEmail = animateurEmail;
        this.sessionCount = sessionCount;
    }
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public List<SessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrixHoraire() {
        return prixHoraire;
    }

    public void setPrixHoraire(double prixHoraire) {
        this.prixHoraire = prixHoraire;
    }

    public Long getAnimateurId() {
        return animateurId;
    }

    public void setAnimateurId(Long animateurId) {
        this.animateurId = animateurId;
    }

    public String getAnimateurFullName() {
        return animateurFullName;
    }

    public void setAnimateurFullName(String animateurFullName) {
        this.animateurFullName = animateurFullName;
    }

    public String getAnimateurEmail() {
        return animateurEmail;
    }

    public void setAnimateurEmail(String animateurEmail) {
        this.animateurEmail = animateurEmail;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }
}