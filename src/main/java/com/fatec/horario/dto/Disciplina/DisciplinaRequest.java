package com.fatec.horario.dto.disciplina;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DisciplinaRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotNull(message = "A carga horária é obrigatória")
    @Positive(message = "A carga horária deve ser um valor positivo")
    Integer cargaHoraria,

    @NotBlank(message = "O tipo da disciplina é obrigatório")
    String tipoDisciplina,

    @NotNull(message = "O período é obrigatório")
    Integer periodo,

    @NotBlank(message = "A modalidade é obrigatória")
    String modalidade,

    @NotBlank(message = "O código da disciplina é obrigatório")
    @Size(min = 3, max = 20)
    String codDisciplina,

    String cor,

    @NotNull(message = "O curso é obrigatório")
    Long idCurso,

    @NotNull(message = "O tipo de sala é obrigatório")
    Long idTipoSala
) {}