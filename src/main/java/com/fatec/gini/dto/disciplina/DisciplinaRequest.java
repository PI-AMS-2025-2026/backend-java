package com.fatec.gini.dto.disciplina;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DisciplinaRequest(
        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotNull(message = "A carga horária é obrigatória")
        @Min(value = 20, message = "A carga horária deve ser no mínimo 20 horas") 
        @Max(value = 160, message = "A carga horária deve ser no máximo 160 horas") 
        Integer cargaHoraria,

        @NotBlank(message = "O tipo da disciplina é obrigatório") String tipoDisciplina,

        @NotNull(message = "O período é obrigatório")
        // CORREÇÃO: impede cadastro com período zero ou negativo
        @Positive(message = "O período deve ser maior que zero") Integer periodo,

        @NotBlank(message = "A modalidade é obrigatória") String modalidade,

        // CORREÇÃO: mensagem customizada para indicar explicitamente que o erro é do
        // campo codDisciplina
        @NotBlank(message = "O código da disciplina é obrigatório") @Size(min = 3, max = 20, message = "O código da disciplina (codDisciplina) deve ter entre 3 e 20 caracteres") String codDisciplina,

        String cor,

    @NotNull(message = "O curso é obrigatório")
    @Valid
    LongDTO curso,

    @NotNull(message = "O tipo de sala é obrigatório")
    @Valid
    LongDTO tipoSala
) {

    public DisciplinaRequest(
            String nome,
            Integer cargaHoraria,
            String tipoDisciplina,
            Integer periodo,
            String modalidade,
            String codDisciplina,
            String cor,
            Long cursoId,
            Long tipoSalaId) {
        this(
                nome,
                cargaHoraria,
                tipoDisciplina,
                periodo,
                modalidade,
                codDisciplina,
                cor,
                cursoId != null ? new LongDTO(cursoId) : null,
                tipoSalaId != null ? new LongDTO(tipoSalaId) : null
        );
    }

    @JsonIgnore
    public Long cursoId() {
        return curso != null ? curso.id() : null;
    }

    @JsonIgnore
    public Long tipoSalaId() {
        return tipoSala != null ? tipoSala.id() : null;
    }
}