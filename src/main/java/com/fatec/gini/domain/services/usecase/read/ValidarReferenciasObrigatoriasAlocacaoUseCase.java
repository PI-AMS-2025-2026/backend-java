package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.infrastructure.repositories.HorarioRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.ParameterException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Valida as referências obrigatórias de uma alocação.
 *
 * <p>
 * Realiza validações de integridade referencial dos seguintes elementos:
 * <ul>
 * <li>Turma - verifica se existe</li>
 * <li>Disciplina - verifica se existe</li>
 * <li>Sala - verifica se existe</li>
 * <li>Usuário - verifica se existe</li>
 * <li>Dia da semana - verifica se é válido</li>
 * <li>Horário - verifica se existe</li>
 * <li>Grade - verifica se existe</li>
 * </ul>
 *
 * <p>
 * <strong>Responsabilidade:</strong> Separar validações de integridade
 * referencial
 * de regras de negócio específicas de alocação.
 */
@Service
@RequiredArgsConstructor
public class ValidarReferenciasObrigatoriasAlocacaoUseCase {

    private final  TurmaRepository turmaRepository;
    private final  DisciplinaRepository disciplinaRepository;
    private final  SalaRepository salaRepository;
    private final  UsuarioRepository usuarioRepository;
    private final  DiaSemanaRepository diaSemanaRepository;
    private final  HorarioRepository horarioRepository;
    private final  GradeHorariaRepository gradeHorariaRepository;

    @Transactional(readOnly = true)
    public Turma buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Disciplina buscarDisciplinaPorId(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Sala buscarSalaPorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioAlteracaoPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário da alteração não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public DiaSemana buscarDiaSemanaPorId(Long id) {
        return diaSemanaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Horario buscarHorarioPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public GradeHoraria buscarGradeHorariaPorId(Long id) {
        if (id == null) {
            throw new ParameterException("Grade horária é obrigatória.");
        }
        return gradeHorariaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horária não encontrada com ID: " + id));
    }

    /**
     * Verifica se a justificativa de alteração é válida (não nula e não vazia).
     * 
     * @param justificativaAlteracao
     * @exception ParameterException se a justificativa for nula ou vazia
     */
    public void validarJustificativaAlteracao(String justificativaAlteracao) {
        if (!StringUtils.hasText(justificativaAlteracao)) {
            throw new ParameterException("A justificativa da alteração é obrigatória para atualizar a alocação.");
        }
    }

    /**
     * Valida as referências obrigatórias de uma alocação.
     * 
     * @param entity
     * @return Alocacao
     * @exception EntityNotFoundException se alguma referência obrigatória for nula
     *                                    ou inválida
     */
    @Transactional(readOnly = true)
    public Alocacao validar(Alocacao entity) {
        Turma turma = buscarTurmaPorId(entity.getTurma().getId());
        Disciplina disciplina = buscarDisciplinaPorId(entity.getDisciplina().getId());
        Sala sala = buscarSalaPorId(entity.getSala().getId());
        Usuario usuario = buscarUsuarioPorId(entity.getUsuario().getId());
        DiaSemana diaSemana = buscarDiaSemanaPorId(entity.getDiaSemana().getId());
        Horario horario = buscarHorarioPorId(entity.getHorario().getId());
        GradeHoraria gradeHoraria = buscarGradeHorariaPorId(entity.getGradeHoraria().getId());

        entity.setTurma(turma);
        entity.setDisciplina(disciplina);
        entity.setSala(sala);
        entity.setUsuario(usuario);
        entity.setDiaSemana(diaSemana);
        entity.setHorario(horario);
        entity.setGradeHoraria(gradeHoraria);

        return entity;
    }

}
