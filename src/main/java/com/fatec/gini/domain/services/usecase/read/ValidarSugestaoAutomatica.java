package com.fatec.gini.domain.services.usecase.read;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * RF06 — Sugestão Automática de Ajuste.
 *
 * Responsável por verificar se uma tentativa de alocação é válida
 * e procurar alternativas compatíveis.
 *
 * Nenhuma alocação é persistida por este UseCase.
 */
@Service
@RequiredArgsConstructor
public class ValidarSugestaoAutomatica {

    private static final int LIMITE_SUGESTOES = 5;

    private final SalaRepository salaRepository;

    private final BlocoHorarioRepository blocoHorarioRepository;

    private final AlocacaoRepository alocacaoRepository;

    private final DisponibilidadeProfessorRepository
            disponibilidadeProfessorRepository;

    private final ValidarCapacidadeSalaUseCase
            validarCapacidadeSalaUseCase;

    private final ValidarDisciplinaTipoSalaUseCase
            validarDisciplinaTipoSalaUseCase;

    private final ValidarAlocacaoUseCase
            validarAlocacaoUseCase;

    /**
     * Identifica o motivo pelo qual uma tentativa
     * de alocação é inválida.
     *
     * Retorna null quando a alocação é válida.
     */
    public String identificarMotivo(Alocacao entity) {

        try {

            validarAlocacaoUseCase.executar(entity);

            return null;

        } catch (BusinessException e) {

            return e.getMessage();
        }
    }

    /**
     * Procura automaticamente alternativas válidas
     * para a alocação.
     *
     * Retorna no máximo 5 sugestões.
     */
    public List<Alocacao> executar(Alocacao entity) {

        List<Alocacao> sugestoes = new ArrayList<>();

        /*
         * Filtra as salas antes dos loops de dia e horário.
         *
         * Capacidade e tipo da sala não dependem
         * do dia ou do bloco de horário.
         */
        List<Sala> salasValidas = buscarSalasValidas(entity);

        List<BlocoHorario> blocosHorarios =
                blocoHorarioRepository.findAll();

        for (DiaSemana diaSemana : DiaSemana.values()) {

            for (BlocoHorario blocoHorario : blocosHorarios) {

                /*
                 * Antes de testar todas as salas, verifica
                 * se o professor pode ser alocado nesse
                 * dia e bloco.
                 */
                if (!professorPodeSerAlocado(
                        entity,
                        diaSemana,
                        blocoHorario)) {

                    continue;
                }

                /*
                 * Verifica se a turma já possui uma
                 * alocação nesse dia e bloco.
                 */
                if (turmaPossuiConflito(
                        entity,
                        diaSemana,
                        blocoHorario)) {

                    continue;
                }

                /*
                 * Apenas agora percorre as salas que
                 * já passaram pela filtragem inicial.
                 */
                for (Sala sala : salasValidas) {

                    /*
                     * Não adiciona como sugestão exatamente
                     * a mesma combinação original.
                     */
                    if (ehMesmaCombinacaoOriginal(
                            entity,
                            sala,
                            diaSemana,
                            blocoHorario)) {

                        continue;
                    }

                    /*
                     * Cria uma entidade temporária somente
                     * para executar as validações completas.
                     *
                     * Esta entidade não é persistida.
                     */
                    Alocacao sugestao = new Alocacao(
                            entity.getTurma(),
                            entity.getDisciplina(),
                            sala,
                            entity.getProfessor(),
                            diaSemana,
                            blocoHorario,
                            entity.getQuadroHorario()
                    );

                    /*
                     * Executa todas as regras restantes
                     * somente para os candidatos que
                     * passaram pelos filtros iniciais.
                     */
                    if (alocacaoValida(sugestao)) {

                        sugestoes.add(sugestao);
                    }

                    /*
                     * Retorna assim que encontrar
                     * 5 sugestões válidas.
                     */
                    if (sugestoes.size() >= LIMITE_SUGESTOES) {

                        return sugestoes;
                    }
                }
            }
        }

        return sugestoes;
    }

    /**
     * Filtra previamente as salas compatíveis.
     *
     * Reutiliza os UseCases existentes para evitar
     * duplicação das regras de capacidade e
     * compatibilidade de tipo de sala.
     */
    private List<Sala> buscarSalasValidas(Alocacao entity) {

        return salaRepository.findAll()
                .stream()
                .filter(sala -> salaPodeSerUtilizada(
                        entity,
                        sala
                ))
                .toList();
    }

    /**
     * Verifica se uma sala pode ser utilizada
     * pela turma e disciplina.
     */
    private boolean salaPodeSerUtilizada(
            Alocacao entity,
            Sala sala) {

        try {

            /*
             * Cria uma entidade temporária para reutilizar
             * a regra existente de capacidade.
             */
            Alocacao tentativa = new Alocacao(
                    entity.getTurma(),
                    entity.getDisciplina(),
                    sala,
                    entity.getProfessor(),
                    entity.getDiaSemana(),
                    entity.getBlocoHorario(),
                    entity.getQuadroHorario()
            );

            validarCapacidadeSalaUseCase.executar(tentativa);

            validarDisciplinaTipoSalaUseCase
                    .validarCompatibilidadeDisciplinaSala(
                            entity.getDisciplina(),
                            sala
                    );

            return true;

        } catch (BusinessException e) {

            return false;
        }
    }

    /**
     * Verifica rapidamente se o professor pode
     * ser utilizado no dia e bloco analisados.
     */
    private boolean professorPodeSerAlocado(
            Alocacao entity,
            DiaSemana diaSemana,
            BlocoHorario blocoHorario) {

        Long professorId =
                entity.getProfessor().getId();

        Long horarioId =
                blocoHorario.getId();

        /*
         * Verifica a disponibilidade cadastrada
         * para o professor.
         */
        boolean possuiDisponibilidade =
                disponibilidadeProfessorRepository
                        .verificarDisponibilidadeProfessor(
                                professorId,
                                diaSemana,
                                horarioId
                        );

        if (!possuiDisponibilidade) {

            return false;
        }

        /*
         * Verifica se o professor já possui outra
         * alocação no mesmo dia e horário.
         */
        boolean possuiSobreposicao =
                alocacaoRepository
                        .existsByProfessorIdAndDiaSemanaAndBlocoHorarioId(
                                professorId,
                                diaSemana,
                                horarioId
                        );

        return !possuiSobreposicao;
    }

    /**
     * Verifica rapidamente se a turma já possui
     * uma alocação no dia e bloco analisados.
     */
    private boolean turmaPossuiConflito(
            Alocacao entity,
            DiaSemana diaSemana,
            BlocoHorario blocoHorario) {

        return alocacaoRepository
                .existsConflitoTurmaHorario(
                        entity.getTurma().getId(),
                        diaSemana,
                        blocoHorario.getId()
                );
    }

    /**
     * Verifica se a combinação candidata é
     * exatamente igual à tentativa original.
     */
    private boolean ehMesmaCombinacaoOriginal(
            Alocacao entity,
            Sala sala,
            DiaSemana diaSemana,
            BlocoHorario blocoHorario) {

        if (entity == null
                || entity.getDiaSemana() == null
                || entity.getBlocoHorario() == null
                || entity.getBlocoHorario().getId() == null
                || entity.getSala() == null
                || entity.getSala().getId() == null
                || sala == null
                || sala.getId() == null
                || blocoHorario == null
                || blocoHorario.getId() == null
                || diaSemana == null) {

            return false;
        }

        return diaSemana == entity.getDiaSemana()
                && blocoHorario.getId().equals(
                        entity.getBlocoHorario().getId()
                )
                && sala.getId().equals(
                        entity.getSala().getId()
                );
    }

    /**
     * Executa todas as validações de negócio
     * para a combinação candidata.
     */
    private boolean alocacaoValida(Alocacao entity) {

        try {

            validarAlocacaoUseCase.executar(entity);

            return true;

        } catch (BusinessException e) {

            return false;
        }
    }
}