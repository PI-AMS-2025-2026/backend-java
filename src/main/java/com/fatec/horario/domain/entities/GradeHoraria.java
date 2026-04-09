package com.fatec.horario.domain.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_periodo_letivo", nullable = false)
    private PeriodoLetivo periodoLetivo;

    public GradeHoraria() {
    }

    public GradeHoraria(Integer versao, LocalDateTime dataCriacao, String status, Curso curso, PeriodoLetivo periodoLetivo) {
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

    public String getStatus() { 
        
        return status; 
    
    }

    public void setStatus(String status) { 
        
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
}