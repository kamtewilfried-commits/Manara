package com.manara.crm.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer capacity = 0;

    private String location;

    private String status;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Registration> registrations;

    @ManyToOne
    @JoinColumn(name = "animateur_id")
    private User animateur;

    public Session() {}

    public double getPrixSession() {
        if (startDate == null || endDate == null || activity == null) {
            return 0;
        }

        double heures = Duration.between(startDate, endDate).toMinutes() / 60.0;
        return heures * activity.getPrixHoraire();
    }

    public int getAvailableSpots() {
        int inscrits = 0;

        if (registrations != null) {
            inscrits = (int) registrations.stream()
                    .filter(r -> "CONFIRMED".equals(r.getStatus()) || "PENDING_PAYMENT".equals(r.getStatus()))
                    .count();
        }

        return capacity - inscrits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public User getAnimateur() {
        return animateur;
    }

    public void setAnimateur(User animateur) {
        this.animateur = animateur;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}