package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CopiarQuadroHorarioUseCase {

    private final QuadroHorarioRepository gradeHorariaRepository;

    private final AlocacaoRepository alocacaoRepository;

    private final ValidarCopiarQuadroHorarioUseCase validarCopiaGradeHorariaUseCase;

    private final ValidarQuadroHorarioValidoUseCase validarGradeHorariaValidaUseCase;

    private final ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

    @Transactional
    public QuadroHorario executar(
            Long idGradeOrigem,
            QuadroHorario dadosNovaGrade) {

        // Busca a grade de origem
        QuadroHorario gradeAnterior =
                gradeHorariaRepository.findById(idGradeOrigem)
                        .orElseThrow(() -> new BusinessException(
                                "Quadro horário de origem não encontrada."));

        // Valida se o curso da grade de origem está ativo
        validarCursoAtivoUseCase.validar(
                gradeAnterior.getCurso());

        // Busca as alocações da grade de origem
        List<Alocacao> alocacoesAnteriores =
                alocacaoRepository.findByQuadroHorarioId(
                        idGradeOrigem);

        // Valida se a grade pode ser copiada
        validarCopiaGradeHorariaUseCase
                .validarRegrasParaCopiaDeGrade(
                        alocacoesAnteriores);

        // Cria a nova grade
        QuadroHorario novoQuadro =
                new QuadroHorario();

        novoQuadro.setVersao(
                dadosNovaGrade.getVersao() != null
                        ? dadosNovaGrade.getVersao() + 1
                        : gradeAnterior.getVersao() + 1);

        novoQuadro.setDataCriacao(
                LocalDateTime.now());

        novoQuadro.setCreatedAt(
                LocalDateTime.now());

        novoQuadro.setUpdatedAt(
                LocalDateTime.now());

        novoQuadro.setStatus(
                Status.ATIVO);

        // A nova grade mantém o mesmo curso da grade de origem
        novoQuadro.setCurso(
                gradeAnterior.getCurso());

        // O período vem da requisição da nova grade
        novoQuadro.setPeriodoAtividadeQuadro(
                dadosNovaGrade.getPeriodoAtividadeQuadro());

        // Valida se já existe outra grade ativa
        // para o mesmo curso e período
        validarGradeHorariaValidaUseCase
                .validarQuadroAtivoDuplicado(
                        novoQuadro);

        // Salva a nova grade
        novoQuadro =
                gradeHorariaRepository.save(
                        novoQuadro);

        // Cria novas alocações
        List<Alocacao> novasAlocacoes =
                new ArrayList<>();

        for (Alocacao antiga : alocacoesAnteriores) {

            Alocacao nova =
                    new Alocacao();

            nova.setQuadroHorario(
                    novoQuadro);

            nova.setTurma(
                    antiga.getTurma());

            nova.setDiaSemana(
                    antiga.getDiaSemana());

            nova.setBlocoHorario(
                    antiga.getBlocoHorario());

            nova.setDisciplina(
                    antiga.getDisciplina());

            nova.setProfessor(
                    antiga.getProfessor());

            nova.setSala(
                    antiga.getSala());

            nova.setCreatedAt(
                    LocalDateTime.now());

            nova.setUpdatedAt(
                    LocalDateTime.now());

            novasAlocacoes.add(
                    nova);
        }

        alocacaoRepository.saveAll(
                novasAlocacoes);

        return novoQuadro;
    }
}