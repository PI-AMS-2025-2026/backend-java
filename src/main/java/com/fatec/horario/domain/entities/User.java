package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tbl_user",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<UserAvailability> availabilities;

    @OneToMany(mappedBy = "user")
    private List<UserSubject> userSubjects;

    @OneToMany(mappedBy = "user")
    private List<CourseUser> courseUsers;

    @OneToMany(mappedBy = "professor")
    private List<Schedule> schedules;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "access_level_id", nullable = false)
    private AccessLevel accessLevel;

    public User() {}

    public User(Long id, String name, String email, String password, AccessLevel accessLevel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    // ==============================
    // Spring Security
    // ==============================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() ->
                accessLevel.getRoleName()
        );
    }

    @Override
    public String getUsername() {
        return email; // login por email
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // ==============================
    // Getters e Setters
    // ==============================

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public AccessLevel getAccessLevel() { return accessLevel; }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<UserAvailability> getAvailabilities() { return availabilities; }

    public List<UserSubject> getUserSubjects() { return userSubjects; }

    public List<CourseUser> getCourseUsers() { return courseUsers; }

    public List<Schedule> getSchedules() { return schedules; }

    // ==============================
    // Equals e HashCode
    // ==============================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}