package com.fatec.gini.domain.services.usecase.write;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.services.usecase.read.ValidarCapacidadeSalaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaDisciplinaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaMaximaProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCoerenciaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoSalaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoTurmaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisciplinaTipoSalaUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class CriarAlocacaoUseCase {
    /**
     * Executa o registro de histórico de alocação.
     */
    private final ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    private final  ValidarDisciplinaTipoSalaUseCase validarDisciplinaTipoSala;

    private final  ValidarCargaHorariaDisciplinaUseCase validarCargaHorariaDisciplina;

    private final  ValidarQuadroHorarioUseCase validarQuadroHorario;

    private final  ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    private final  ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    private final  ValidarConflitoTurmaBlocoHorarioUseCase validarConflitoTurmaHorario;

    private final  ValidarConflitoSalaBlocoHorarioUseCase validarConflitoSalaHorario;

    private final  ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    private final  ValidarCapacidadeSalaUseCase validarCapacidadeSala;

    private final  RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    private final  AlocacaoRepository alocacaoRepository;    

    private final  ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    private final  ValidarCoerenciaUseCase validarCoerenciaCurso;


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
        validarCargaHorariaUseCase.validar(entity.getProfessor().getId(), entity.getDiaSemana());

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
