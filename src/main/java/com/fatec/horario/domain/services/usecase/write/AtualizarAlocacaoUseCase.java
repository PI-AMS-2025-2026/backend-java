package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.services.usecase.read.ValidarCapacidadeSalaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarCargaHorariaDisciplinaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarCargaHorariaMaximaProfessorUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarConflitoTurmaHorarioUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDisciplinaTipoSalaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarGradeHorariaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.domain.services.usecase.read.ValidarCoerenciaUseCase;

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
    private ValidarCargaHorariaDisciplinaUseCase validarCargaHorariaDisciplina;

    @Autowired
    private ValidarConflitoTurmaHorarioUseCase validarConflitoTurmaHorario;

    @Autowired
    private ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    @Autowired
    private ValidarCapacidadeSalaUseCase validarCapacidadeSala;

    @Autowired
    private RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    @Autowired
    private ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    @Autowired
    private ValidarDisciplinaTipoSalaUseCase validarDisciplinaTipoSala;

    @Autowired
    private ValidarCoerenciaUseCase validarCoerenciaCurso;

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Transactional
    public Alocacao executar(Long id, Alocacao entity, long usuarioAlteracao, String justificativaAlteracao) {

        // Verifica se dados em entidade são válidos e existem
        validarRefObrigatorias.validar(entity);
        var usuarioAlteracaoEntity = validarRefObrigatorias.buscarUsuarioAlteracaoPorId(usuarioAlteracao);
        // validarRefObrigatorias.validarJustificativaAlteracao(justificativaAlteracao);

        // valida a compatibilidade da disciplina com a sala alocada
         validarDisciplinaTipoSala.validarCompatibilidadeDisciplinaSala(
                entity.getDisciplina(), 
                entity.getSala()
        );

        // Validar limite máximo da carga horária total da disciplina 
        validarCargaHorariaDisciplina.executar(entity);

        // Validar se o professor já atingiu a carga horária máxima diária para o dia da
        // semana da alocação
        validarCargaHorariaUseCase.validar(entity.getUsuario().getId(), entity.getDiaSemana().getId());

        // Validar grade horaria
        validarGradeHoraria.executar(entity);

        // Validar vinculo professor disciplina
        validarVincProfDisciplina.executar(entity);

        // validar disponibilidade do professor
        validarDisponibilidadeProfessor.executar(entity);
        // validar conflito da turma
        validarConflitoTurmaHorario.executar(entity);

        // validar capacidade da sala
        validarCapacidadeSala.executar(entity);

        // validar duplicidade
        validarDuplicidade.executarAtualizacao(entity);

        // salvar alocação
        Alocacao alocacaoSalva = alocacaoRepository.save(entity);

        // registrar histórico de atualização
        historicoAlocacaoUseCase.registrarAtualizacao(alocacaoSalva, entity, usuarioAlteracaoEntity,
                justificativaAlteracao);

        // validar coerência entre curso da grade, turma e disciplina
        validarCoerenciaCurso.validar(entity);

        return alocacaoSalva;
    }
}
