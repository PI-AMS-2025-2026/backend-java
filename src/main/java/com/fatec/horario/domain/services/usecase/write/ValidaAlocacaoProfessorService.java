package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.infrastructure.repositories.DisponibilidadeProfessorRepository;

@Service
public class ValidaAlocacaoProfessorService {

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    public void validarProfessorPodeSerAlocado(Long usuarioId, Long diaId, Long horarioId) {
        validarDisponibilidadeProfessor(usuarioId, diaId, horarioId);
        validarConflitoHorarioProfessor(usuarioId, diaId, horarioId);
    }

    // ✅ REGRA 1: Disponibilidade
    private void validarDisponibilidadeProfessor(Long usuarioId, Long diaId, Long horarioId) {

        boolean possuiDisponibilidade = alocacaoRepository
                .verificarDisponibilidadeProfessor(usuarioId, diaId, horarioId);

        if (!possuiDisponibilidade) {
            throw new BusinessException("Professor não possui disponibilidade para o dia e horário informado.");
        }
    }

    // ✅ REGRA 2: Conflito
    private void validarConflitoHorarioProfessor(Long usuarioId, Long diaId, Long horarioId) {

        boolean possuiConflito = alocacaoRepository
                .existsByUsuarioIdAndDiaSemanaIdAndHorarioId(usuarioId, diaId, horarioId);

        if (possuiConflito) {
            throw new BusinessException("Professor já está alocado em outra turma/disciplina neste dia e horário.");
        }
    }
}

