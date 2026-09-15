package com.fatec.gini.domain.services.usecase.write;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.services.usecase.read.ValidarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

import lombok.RequiredArgsConstructor;

/**
 * UseCase responsável pelo processo completo de criação
 * de alocações.
 */
@Service
@RequiredArgsConstructor
public class CriarAlocacaoUseCase {

    private final ValidarAlocacaoUseCase validarAlocacaoUseCase;

    private final ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    private final RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    private final AlocacaoRepository alocacaoRepository;

    @Transactional
    public Alocacao executar(
            Alocacao entity,
            long usuarioAlteracao) {

        /*
         * Busca e valida o usuário responsável pela alteração.
         *
         * Essa validação permanece aqui porque o usuário não faz
         * parte das regras da alocação utilizadas na sugestão automática.
         */
        var usuarioAlteracaoEntity =
                validarRefObrigatorias.buscarUsuarioAlteracaoPorId(
                        usuarioAlteracao
                );

        /*
         * Executa todas as regras de negócio centralizadas.
         */
        validarAlocacaoUseCase.executar(entity);

        /*
         * Salva a alocação após todas as validações.
         */
        Alocacao alocacaoSalva =
                alocacaoRepository.save(entity);

        /*
         * Registra o histórico da criação.
         */
        historicoAlocacaoUseCase.registrarCriacao(
                alocacaoSalva,
                usuarioAlteracaoEntity
        );

        return alocacaoSalva;
    }
}