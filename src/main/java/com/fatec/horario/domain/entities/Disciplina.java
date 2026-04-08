package com.fatec.horario.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "disciplina")
public class Disciplina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disciplina")
    private Long idDisciplina;

    private String nome;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    @Column(name = "tipo_disciplina")
    private String tipoDisciplina;

    private Integer periodo;
    private String modalidade;

    @Column(name = "cod_disciplina")
    private String codDisciplina;

    private String cor;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_tipo_sala")
    private TipoSala tipoSala;

    public Disciplina() {
    }

    public Disciplina(String nome, Integer cargaHoraria, String tipoDisciplina, Integer periodo,
            String modalidade, String codDisciplina, String cor, Curso curso, TipoSala tipoSala) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.tipoDisciplina = tipoDisciplina;
        this.periodo = periodo;
        this.modalidade = modalidade;
        this.codDisciplina = codDisciplina;
        this.cor = cor;
        this.curso = curso;
        this.tipoSala = tipoSala;
    }

    //Getters e Setters
    public Long getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Long idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getTipoDisciplina() {
        return tipoDisciplina;
    }

    public void setTipoDisciplina(String tipoDisciplina) {
        this.tipoDisciplina = tipoDisciplina;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getCodDisciplina() {
        return codDisciplina;
    }

    public void setCodDisciplina(String codDisciplina) {
        this.codDisciplina = codDisciplina;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public TipoSala getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(TipoSala tipoSala) {
        this.tipoSala = tipoSala;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idDisciplina == null) ? 0 : idDisciplina.hashCode());
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
        Disciplina other = (Disciplina) obj;
        if (idDisciplina == null) {
            if (other.idDisciplina != null)
                return false;
        } else if (!idDisciplina.equals(other.idDisciplina))
            return false;
        return true;
    }
}
