package com.fatec.gini.domain.services.usecase.read;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCopiarGradeHorariaUseCase {

    private final  DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

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
                    || antiga.getProfessor() == null
                    || antiga.getSala() == null
                    || antiga.getDiaSemana() == null
                    || antiga.getBlocoHorario() == null) {

                throw new BusinessException(
                        "Dados de alocação de origem corrompidos ou incompletos.");
            }
            /*TODO: Chamar verificarDisponibilidadeProfessor(...) dentro do loop pode gerar padrão N+1 (uma consulta por alocação). Considere validar em lote (ex.: buscar disponibilidades para todos os (professor,dia,horário) da lista e validar em memória) para reduzir round-trips ao banco.*/
            // valida disponibilidade do professor via id
            boolean disponivel =
                    disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(
                            antiga.getProfessor().getId(),
                            antiga.getDiaSemana().getId(),
                            antiga.getBlocoHorario().getId());

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