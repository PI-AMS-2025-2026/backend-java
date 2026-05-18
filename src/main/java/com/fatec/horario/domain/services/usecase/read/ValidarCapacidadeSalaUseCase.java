package com.fatec.horario.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.web.exception.BusinessException;

/**
 * Valida a capacidade da sala em relação ao número de alunos da turma.
 * 
 * <p>
 * Responsável por garantir que a infraestrutura alocada seja adequada ao
 * tamanho da turma, impedindo superlotação e garantindo o conforto dos alunos.
 * </p>
 * 
 * <p>
 * <strong>Critério de validação:</strong> A capacidade física da sala deve ser
 * maior ou igual ao número de alunos matriculados na turma.
 * </p>
 * 
 * <p>
 * Exemplo: Uma turma com 40 alunos não pode ser alocada em uma sala com
 * capacidade de 30 lugares.
 * </p>
 */
@Service
public class ValidarCapacidadeSalaUseCase {

    /**
     * Valida se a sala possui capacidade suficiente para a turma.
     * 
     * @param entity Alocação contendo turma e sala
     * @throws BusinessException se a capacidade da sala for inferior ao número de
     *                           alunos da turma
     */
    public void executar(Alocacao entity) {
        Integer capacidadeSala = entity.getSala().getCapacidade();
        Integer numeroAlunos = entity.getTurma().getNumeroAlunos();

        if (capacidadeSala < numeroAlunos) {
            throw new BusinessException(
                    "A sala selecionada não possui capacidade suficiente para comportar os alunos desta turma. "
                            + "Capacidade: " + capacidadeSala + ", Alunos: " + numeroAlunos);
        }
    }
}
