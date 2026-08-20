package com.fatec.gini.domain.services.usecase.read;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * RF06 — Sugestão Automática de Ajuste.
 *
 * Responsável por verificar se uma tentativa de alocação é válida.
 *
 * Caso exista alguma incompatibilidade, procura automaticamente
 * combinações alternativas de:
 *
 * - sala;
 * - dia da semana;
 * - bloco de horário.
 *
 * As sugestões são apenas avaliadas.
 * Nenhuma alocação é persistida por este UseCase.
 */
@Service
@RequiredArgsConstructor
public class ValidarSugestaoAutomatica {

    private static final int LIMITE_SUGESTOES = 5;

    private final SalaRepository salaRepository;

    private final BlocoHorarioRepository blocoHorarioRepository;

    private final ValidarAlocacaoUseCase validarAlocacaoUseCase;

    /**
     * Identifica o motivo pelo qual uma tentativa de alocação é inválida.
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
     * Procura automaticamente alternativas válidas para a alocação.
     *
     * São avaliadas diferentes combinações de:
     *
     * - dia da semana;
     * - bloco de horário;
     * - sala.
     *
     * Retorna no máximo 5 sugestões.
     */
    public List<Alocacao> executar(Alocacao entity) {

        List<Alocacao> sugestoes = new ArrayList<>();

        List<Sala> salas =
                salaRepository.findAll();

        List<BlocoHorario> blocosHorarios =
                blocoHorarioRepository.findAll();

        for (DiaSemana diaSemana : DiaSemana.values()) {

            for (BlocoHorario blocoHorario : blocosHorarios) {

                for (Sala sala : salas) {

                    /*
                     * Não adiciona como sugestão exatamente
                     * a mesma combinação que o usuário tentou.
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
                     * para executar as validações.
                     *
                     * Esta entidade não é salva.
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
                     * Verifica se a combinação candidata
                     * atende todas as regras de negócio.
                     */
                    if (alocacaoValida(sugestao)) {

                        sugestoes.add(sugestao);
                    }

                    /*
                     * Limita a quantidade de sugestões.
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
     * Verifica se a combinação candidata é exatamente
     * a mesma que o usuário tentou originalmente.
     */
    private boolean ehMesmaCombinacaoOriginal(
            Alocacao entity,
            Sala sala,
            DiaSemana diaSemana,
            BlocoHorario blocoHorario) {

        /*
         * Proteção contra NullPointerException.
         *
         * Caso a alocação original não possua sala,
         * bloco de horário ou dia da semana, não é possível
         * considerá-la igual à combinação candidata.
         */
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
     * Verifica se uma combinação candidata é válida.
     *
     * Caso alguma regra de negócio seja violada,
     * a combinação é descartada.
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