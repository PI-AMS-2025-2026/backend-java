package com.fatec.horario.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "grade_horaria")
public class GradeHoraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grade_horaria")
    private Long id;

    @Column(nullable = false)
    private Integer versao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_periodo_letivo", nullable = false)
    private PeriodoLetivo periodoLetivo;

    @OneToMany(mappedBy = "gradeHoraria")
    private List<Alocacao> alocacoes;

    public GradeHoraria() {
    }

    public GradeHoraria(Integer versao, LocalDateTime dataCriacao, Status status, Curso curso,
            PeriodoLetivo periodoLetivo) {
        this.versao = versao;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.curso = curso;
        this.periodoLetivo = periodoLetivo;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public PeriodoLetivo getPeriodoLetivo() {
        return periodoLetivo;
    }

    public void setPeriodoLetivo(PeriodoLetivo periodoLetivo) {
        this.periodoLetivo = periodoLetivo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        // Ajustado de PeriodoLetivo para GradeHoraria
        GradeHoraria other = (GradeHoraria) obj;

        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}