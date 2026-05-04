package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.services.usecase.read.ValidarConflitoTurmaHorarioUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarGradeHorariaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;

/**
 * UseCase responsável por orquestrar o processo completo de criação de
 * alocações.
 *
 * <p>
 * Executa as seguintes etapas:
 * </p>
 * <ul>
 * <li>Recebe e processa o comando/DTO de criação</li>
 * <li>Valida a existência dos dados base necessários</li>
 * <li>Executa as validações de regras de negócio</li>
 * <li>Persiste a alocação no repositório</li>
 * <li>Registra o histórico de operação, quando aplicável</li>
 * </ul>
 */
@Service
public class AtualizarAlocacaoUseCase {
    /**
     * Executa o registro de histórico de alocação.
     */
    @Autowired
    private ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    @Autowired
    private ValidarGradeHorariaUseCase validarGradeHoraria;

    @Autowired
    private ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    @Autowired
    private ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    @Autowired
    private ValidarConflitoTurmaHorarioUseCase validarConflitoTurmaHorario;

    @Autowired
    private ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    @Autowired
    private RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Transactional
    public Alocacao executar(Long id, Alocacao entity, long usuarioAlteracao, String justificativaAlteracao) {

        // Verifica se dados em entidade são válidos e existem
        validarRefObrigatorias.validar(entity);
        var usuarioAlteracaoEntity = validarRefObrigatorias.buscarUsuarioAlteracaoPorId(usuarioAlteracao);
        // validarRefObrigatorias.validarJustificativaAlteracao(justificativaAlteracao);

        // Validar grade horaria
        validarGradeHoraria.executar(entity);

        // Validar vinculo professor disciplina
        validarVincProfDisciplina.executar(entity);

        // validar disponibilidade do professor
        validarDisponibilidadeProfessor.executar(entity);
        // validar conflito da turma
        validarConflitoTurmaHorario.executar(entity);

        // validar duplicidade
        validarDuplicidade.executarAtualizacao(entity);

        // salvar alocação
        Alocacao alocacaoSalva = alocacaoRepository.save(entity);

        // registrar histórico de atualização
        historicoAlocacaoUseCase.registrarAtualizacao(alocacaoSalva, entity, usuarioAlteracaoEntity,
                justificativaAlteracao);

        return alocacaoSalva;
    }
}
