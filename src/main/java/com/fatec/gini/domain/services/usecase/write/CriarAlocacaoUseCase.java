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

        // Validar limite máximo da carga horária total da disciplina
        validarCargaHorariaDisciplina.executar(entity);

        // Validar se o professor já atingiu a carga horária máxima diária para o dia da
        // semana da alocação
        validarCargaHorariaUseCase.validar(entity.getProfessor().getId(), entity.getDiaSemana(), entity);

        // Validar quadro horário
        validarQuadroHorario.executar(entity);

        // Validar vinculo professor disciplina
        validarVincProfDisciplina.executar(entity);

        // validar disponibilidade do professor
        validarDisponibilidadeProfessor.executar(entity);
        // validar conflito da turma
        validarConflitoTurmaHorario.executar(entity);

        // validar conflito da sala
        validarConflitoSalaHorario.executar(entity);

        // validar capacidade da sala
        validarCapacidadeSala.executar(entity);

        // validar duplicidade
        validarDuplicidade.executarCriacao(entity);

        // salvar alocação
        Alocacao alocacaoSalva = alocacaoRepository.save(entity);

        // registrar histórico de criação
        historicoAlocacaoUseCase.registrarCriacao(alocacaoSalva, usuarioAlteracaoEntity);       

        // validar coerência entre curso do quadro horário, turma e disciplina
        validarCoerenciaCurso.validar(entity);

        return alocacaoSalva;
    }
}