package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String cidade;

    private Boolean status;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "id_tipo_usuario", nullable = false)
    private TipoUsuario tipo_usuario;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha,
                   String cidade, Boolean status, LocalDateTime created_at,
                   LocalDateTime updated_at, TipoUsuario tipo_usuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cidade = cidade;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.tipo_usuario = tipo_usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public TipoUsuario getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(TipoUsuario tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != null && status;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != null && status;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status != null && status;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status;
    }
}