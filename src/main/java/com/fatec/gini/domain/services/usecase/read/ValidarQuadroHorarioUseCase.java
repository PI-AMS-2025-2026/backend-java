package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Responsável por garantir que o quadro horário da alocação é válido.
 * 
 * <p>
 * Valida:
 * </p>
 * <li>quadro horário existe</li>
 * <li>quadro horário está ativa</li>
 * <li>quadro horário é a última versão</li>
 * 
 */
@Service
@RequiredArgsConstructor
public class ValidarQuadroHorarioUseCase {

    private final QuadroHorarioRepository quadroHorarioRepository;

    public void executar(Alocacao entity) {
        var quadroHorario = entity.getQuadroHorario();
        quadroHorarioAtiva(quadroHorario);
        quadroHorarioUltimaVersao(quadroHorario);

    }

    private void quadroHorarioAtiva(QuadroHorario quadroHorario) {
        if (quadroHorario.getStatus() != Status.ATIVO) {
            throw new BusinessException("O quadro horário informada deve estar ativa.");
        }
    }

    private void quadroHorarioUltimaVersao(QuadroHorario quadroHorario) {
        QuadroHorario ultimaquadroHorario = quadroHorarioRepository
                .findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(
                        quadroHorario.getCurso().getId(),
                        quadroHorario.getPeriodoLetivo().getId())
                .orElseThrow(
                        () -> new BusinessException("Não foi possível validar a última versão da quadro horário."));

        if (!quadroHorario.getId().equals(ultimaquadroHorario.getId())) {
            throw new BusinessException("O quadro horário informado não é a última versão disponível.");
        }
    }

}
