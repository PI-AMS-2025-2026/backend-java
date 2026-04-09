package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer capacidade;

    @ManyToOne
    @JoinColumn(name = "id_tipo_sala", nullable = false)
    private TipoSala tipoSala;

    public Sala() {
    }

    public Sala(Long id, String codigo, Integer capacidade, TipoSala tipoSala) {
        this.id = id;
        this.codigo = codigo;
        this.capacidade = capacidade;
        this.tipoSala = tipoSala;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public TipoSala getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(TipoSala tipoSala) {
        this.tipoSala = tipoSala;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sala other = (Sala) obj;
        return Objects.equals(id, other.id);
    }
}