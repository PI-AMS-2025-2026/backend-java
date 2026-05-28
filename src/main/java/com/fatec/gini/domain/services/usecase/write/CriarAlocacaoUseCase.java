package com.fatec.gini.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.services.usecase.read.ValidarCapacidadeSalaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaDisciplinaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaMaximaProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCoerenciaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoSalaHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoTurmaHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisciplinaTipoSalaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarGradeHorariaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

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
public class CriarAlocacaoUseCase {
    /**
     * Executa o registro de histórico de alocação.
     */
    @Autowired
    private ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    @Autowired
    private ValidarDisciplinaTipoSalaUseCase validarDisciplinaTipoSala;

    @Autowired
    private ValidarCargaHorariaDisciplinaUseCase validarCargaHorariaDisciplina;

    @Autowired
    private ValidarGradeHorariaUseCase validarGradeHoraria;

    @Autowired
    private ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    @Autowired
    private ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    @Autowired
    private ValidarConflitoTurmaHorarioUseCase validarConflitoTurmaHorario;

    @Autowired
    private ValidarConflitoSalaHorarioUseCase validarConflitoSalaHorario;

    @Autowired
    private ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    @Autowired
    private ValidarCapacidadeSalaUseCase validarCapacidadeSala;

    @Autowired
    private RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    @Autowired
    private AlocacaoRepository alocacaoRepository;    

    @Autowired
    private ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    @Autowired
    private ValidarCoerenciaUseCase validarCoerenciaCurso;

    @Transactional
    public Alocacao executar(Alocacao entity, long usuarioAlteracao) {

        // Verifica se dados em entidade são válidos e existem
        validarRefObrigatorias.validar(entity);
        var usuarioAlteracaoEntity = validarRefObrigatorias.buscarUsuarioAlteracaoPorId(usuarioAlteracao);

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

        // validar coerência entre curso da grade, turma e disciplina
        validarCoerenciaCurso.validar(entity);

        return alocacaoSalva;
    }
}
