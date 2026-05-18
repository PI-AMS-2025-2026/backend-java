package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidarCopiarGradeHorariaUseCase {

    @Autowired
    private DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    @Transactional(readOnly = true)
    public void validarRegrasParaCopiaDeGrade(List<Alocacao> alocacoes) {

        if (alocacoes.isEmpty()) {
            throw new BusinessException(
                    "Não é possível copiar uma grade horária que não possui alocações.");
        }

        // percorre todas as alocações da grade antiga
        for (Alocacao antiga : alocacoes) {

            //ve se existe dados obrigatorios
            if (antiga.getDisciplina() == null
                    || antiga.getUsuario() == null
                    || antiga.getSala() == null) {

                throw new BusinessException(
                        "Dados de alocação de origem corrompidos ou incompletos.");
            }

            // valida disponibilidade do professor via id
            boolean disponivel =
                    disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(
                            antiga.getUsuario().getId(),
                            antiga.getDiaSemana().getId(),
                            antiga.getHorario().getId());

            if (!disponivel) {
                throw new BusinessException(
                        "Professor sem disponibilidade para o horário copiado.");
            }

            // valida compatibilidade de sala
            boolean tipoCompativel =
                    antiga.getDisciplina().getTipoSala().getId()
                            .equals(antiga.getSala().getTipoSala().getId());

            if (!tipoCompativel) {
                throw new BusinessException(
                        "O tipo de sala não atende aos requisitos da disciplina.");
            }
        }
    }
}