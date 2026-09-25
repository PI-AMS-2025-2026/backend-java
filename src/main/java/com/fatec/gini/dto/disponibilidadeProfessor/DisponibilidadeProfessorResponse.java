package com.fatec.gini.dto.disponibilidadeProfessor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;

public record DisponibilidadeProfessorResponse(

        Long id,

        ProfessorResponse professor,

        // CORREÇÃO: garante serialização de diaSemana como string e padroniza o nome do campo no JSON
        // para snake_case (dia_semana), alinhado ao padrão já usado nos @RequestParam do endpoint
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        @JsonProperty("dia_semana")
        DiaSemana diaSemana,


        // CORREÇÃO: renomeia o campo no JSON de resposta para bloco_horario;
        // o valor já estava correto, mas o nome camelCase (blocoHorario) não batia
        // com o que os testes esperavam, parecendo null
        @JsonProperty("bloco_horario")
        BlocoHorarioResponse blocoHorario,

        LocalDateTime created_at,

        LocalDateTime updated_at) {

}