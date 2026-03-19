package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "access_level")
public class AccessLevel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer level;

    private String description;

    @OneToMany(mappedBy = "accessLevel")
    private List<Usuario> users;

    public AccessLevel() {}

    public AccessLevel(Long id, Integer level, String description) {
        this.id = id;
        this.level = level;
        this.description = description;
    }

    // ==============================
    // Método importante para Security
    // ==============================

    public String getRoleName() {

        return switch (level) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_PROFESSOR";
            default -> "ROLE_USER";
        };
    }

    // ==============================
    // Getters e Setters
    // ==============================

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Usuario> getUsers() { return users; }

    public void setUsers(List<Usuario> users) { this.users = users; }

    // ==============================
    // Equals e HashCode
    // ==============================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessLevel that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}