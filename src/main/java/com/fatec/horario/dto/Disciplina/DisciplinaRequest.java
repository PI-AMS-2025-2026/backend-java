package com.fatec.horario.dto.Disciplina;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DisciplinaRequest {

    @NotBlank(message = "O nome da disciplina é obrigatório")
    private String nome;

    @NotNull(message = "A carga horária é obrigatória")
    @Positive(message = "A carga horária deve ser positiva")
    private Integer cargaHoraria;

    private String tipoDisciplina;
    
    private Integer periodo;

    private String modalidade;

    @NotBlank(message = "O código da disciplina é obrigatório")
    private String codDisciplina;

    private String cor;

    @NotNull(message = "O curso é obrigatório")
    private Long idCurso;

    @NotNull(message = "O tipo de sala é obrigatório")
    private Long idTipoSala;

    //Getters e Setters

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

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdTipoSala() {
        return idTipoSala;
    }

    public void setIdTipoSala(Long idTipoSala) {
        this.idTipoSala = idTipoSala;
    }
    
    
}
