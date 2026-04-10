package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false)
    private Integer capacidade;

    @ManyToOne(fetch = FetchType.LAZY)
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

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public TipoSala getTipoSala() { return tipoSala; }
    public void setTipoSala(TipoSala tipoSala) { this.tipoSala = tipoSala; }
}