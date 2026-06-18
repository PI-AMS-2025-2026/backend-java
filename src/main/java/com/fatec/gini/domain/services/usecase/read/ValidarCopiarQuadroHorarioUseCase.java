package com.fatec.gini.domain.services.usecase.read;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeKey;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCopiarQuadroHorarioUseCase {

    private final DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    @Transactional(readOnly = true)
    public void validarRegrasParaCopiaDeGrade(List<Alocacao> alocacoes) {

        if (alocacoes.isEmpty()) {
            throw new BusinessException(
                    "Não é possível copiar um quadro horário que não possui alocações.");
        }

        List<DisponibilidadeProfessor> disponibilidades = disponibilidadeProfessorRepository
                .findByProfessorIdIn(alocacoes.stream()
                        .map(a -> a.getProfessor().getId())
                        .distinct()
                        .toList());

        Set<DisponibilidadeKey> disponibilidadesSet = disponibilidades.stream()
                .map(d -> new DisponibilidadeKey(
                        d.getProfessor().getId(),
                        d.getDiaSemana().getId(),
                        d.getBlocoHorario().getId()))
                .collect(Collectors.toSet());

        // percorre todas as alocações da grade antiga
        for (Alocacao antiga : alocacoes) {

            // ve se existe dados obrigatorios
            if (antiga.getDisciplina() == null
                    || antiga.getProfessor() == null
                    || antiga.getSala() == null
                    || antiga.getDiaSemana() == null
                    || antiga.getBlocoHorario() == null) {

                throw new BusinessException(
                        "Dados de alocação de origem corrompidos ou incompletos.");
            }
            
            boolean disponivel = disponibilidadesSet.contains(
                    new DisponibilidadeKey(
                            antiga.getProfessor().getId(),
                            antiga.getDiaSemana().getId(),
                            antiga.getBlocoHorario().getId()));

            if (!disponivel) {
                throw new BusinessException(
                        "Professor sem disponibilidade para o horário copiado.");
            }

            // valida compatibilidade de sala
            boolean tipoCompativel = antiga.getDisciplina().getTipoSala().getId()
                    .equals(antiga.getSala().getTipoSala().getId());

            if (!tipoCompativel) {
                throw new BusinessException(
                        "O tipo de sala não atende aos requisitos da disciplina.");
            }
        }
    }
}