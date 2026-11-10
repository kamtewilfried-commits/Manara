package com.manara.crm.entity;




import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    private double prixHoraire;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)

    private List<Session> sessions;
    private boolean active = false;

    @ManyToOne
    @JoinColumn(name = "animateur_id")


    private User animateur;

    private String categorie;
    @Column(nullable = true)
    private String photo;
    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public User getAnimateur() {
        return animateur;
    }

    public void setAnimateur(User animateur) {
        this.animateur = animateur;
    }

    public Activity() {}

    public Activity(Long id, String name, String description, List<Session> sessions, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sessions = sessions;
        this.active = active;
    }

    public Activity(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public double getPrixHoraire() {
        return prixHoraire;
    }

    public void setPrixHoraire(double prixHoraire) {
        this.prixHoraire = prixHoraire;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
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

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void setId(Long id) {

            this.id = id;

    }


}
