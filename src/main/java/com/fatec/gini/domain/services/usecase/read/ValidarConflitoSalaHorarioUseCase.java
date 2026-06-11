package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarConflitoSalaHorarioUseCase {

    // Repository responsável pelas consultas de alocação
    private final  AlocacaoRepository repository;

    /**
     * Valida se já existe uma alocação para a mesma sala,
     * no mesmo dia da semana e horário.
     * 
     * Caso exista conflito, uma BusinessException será lançada.
     */
    public void executar(Alocacao entity) {

        // Verifica no banco se a sala já está ocupada
        boolean salaOcupada =
                repository.existsBySalaIdAndDiaSemanaIdAndHorarioId(
                        entity.getSala().getId(),
                        entity.getDiaSemana().getId(),
                        entity.getHorario().getId()
                );

        // Lança exceção caso exista conflito de alocação
        if (salaOcupada) {
            throw new BusinessException(
                "A sala selecionada já está ocupada por outra turma neste dia e horário."
            );
        }
    }
}