package com.manara.crm.entity;

import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @Column(length = 40, nullable = false, unique = true)

    private String nom;
    @Column(length = 150, nullable = false)

    private String description;


    public Role() {
    }

    public Role(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Role(String nom) {
        this.nom = nom;
    }

    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }
    public Role(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String afficherTitreDesColonnes() {
        String message = "";
        message = String.format(" %-10s  %15s %30s ", "Id", "Nom", "Description");
        message+="\n --------------------------------------------------------------------------------------------------------------------------------------";
        return message;
    }
    @Override
    public String toString() {
        String message = "";
        message = String.format("  %-10s  %15s %30s",this.id,this.nom, this.description);
        return message;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}
