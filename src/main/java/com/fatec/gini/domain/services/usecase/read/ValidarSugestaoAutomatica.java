package com.fatec.gini.domain.services.usecase.read;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
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

    private final ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    private final ValidarDisciplinaTipoSalaUseCase validarDisciplinaTipoSala;

    private final ValidarCargaHorariaDisciplinaUseCase validarCargaHorariaDisciplina;

    private final ValidarQuadroHorarioUseCase validarQuadroHorario;

    private final ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    private final ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    private final ValidarConflitoTurmaBlocoHorarioUseCase validarConflitoTurmaHorario;

    private final ValidarConflitoSalaBlocoHorarioUseCase validarConflitoSalaHorario;

    private final ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    private final ValidarCapacidadeSalaUseCase validarCapacidadeSala;

    private final ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaProfessor;

    private final ValidarCoerenciaUseCase validarCoerenciaCurso;


    /**
     * Identifica o motivo pelo qual uma tentativa de alocação é inválida.
     *
     * Retorna null quando a alocação é válida.
     */
    public String identificarMotivo(Alocacao entity) {

        try {

            executarTodasValidacoes(entity);

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

        List<Sala> salas = salaRepository.findAll();

        List<BlocoHorario> blocosHorarios =
                blocoHorarioRepository.findAll();

        /*
         * Testa todos os dias disponíveis.
         */
        for (DiaSemana diaSemana : DiaSemana.values()) {

            /*
             * Testa todos os blocos de horário cadastrados.
             */
            for (BlocoHorario blocoHorario : blocosHorarios) {

                /*
                 * Testa todas as salas cadastradas.
                 */
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
                     * Esta entidade NÃO é salva.
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
                     * atende TODAS as regras de criação.
                     */
                    if (alocacaoValida(sugestao)) {

                        sugestoes.add(sugestao);
                    }

                    /*
                     * Limita a quantidade de sugestões
                     * retornadas ao frontend.
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
     * Executa todas as validações utilizadas no processo
     * de criação de uma alocação.
     *
     * Importante:
     * nenhuma operação de persistência é realizada aqui.
     */
    private void executarTodasValidacoes(Alocacao entity) {

        /*
         * Verifica referências obrigatórias.
         */
        validarRefObrigatorias.validar(entity);


        /*
         * Verifica se a disciplina pode utilizar
         * o tipo de sala selecionado.
         */
        validarDisciplinaTipoSala.validarCompatibilidadeDisciplinaSala(
                entity.getDisciplina(),
                entity.getSala()
        );


        /*
         * Verifica se a disciplina ainda possui
         * carga horária disponível no quadro.
         */
        validarCargaHorariaDisciplina.executar(entity);


        /*
         * Verifica se o professor não ultrapassou
         * a carga horária máxima permitida no dia.
         */
        validarCargaHorariaProfessor.validar(
                entity.getProfessor().getId(),
                entity.getDiaSemana()
        );


        /*
         * Verifica se o bloco de horário pertence
         * ao quadro horário informado.
         */
        validarQuadroHorario.executar(entity);


        /*
         * Verifica se o professor possui vínculo
         * com a disciplina.
         */
        validarVincProfDisciplina.executar(entity);


        /*
         * Verifica disponibilidade do professor
         * e conflito com outra atividade.
         */
        validarDisponibilidadeProfessor.executar(entity);


        /*
         * Verifica se a turma já possui uma
         * alocação no mesmo dia e horário.
         */
        validarConflitoTurmaHorario.executar(entity);


        /*
         * Verifica se a sala já está ocupada
         * naquele dia e horário.
         */
        validarConflitoSalaHorario.executar(entity);


        /*
         * Verifica se a capacidade da sala é
         * suficiente para a quantidade de alunos.
         */
        validarCapacidadeSala.executar(entity);


        /*
         * Verifica se não existe uma alocação
         * duplicada.
         */
        validarDuplicidade.executarCriacao(entity);


        /*
         * Verifica a coerência entre curso,
         * quadro horário, turma e disciplina.
         */
        validarCoerenciaCurso.validar(entity);
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

        return diaSemana == entity.getDiaSemana()
                && blocoHorario.getId().equals(
                        entity.getBlocoHorario().getId())
                && sala.getId().equals(
                        entity.getSala().getId());
    }


    /**
     * Verifica se uma combinação candidata é válida.
     *
     * Caso qualquer regra seja violada, a combinação
     * é simplesmente descartada.
     */
    private boolean alocacaoValida(Alocacao entity) {

        try {

            executarTodasValidacoes(entity);

            return true;

        } catch (BusinessException e) {

            return false;
        }
    }
}