package com.fatec.gini.domain.services.usecase.read;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeKey;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso responsável por validar as regras de negócio necessárias para permitir
 * a cópia de uma grade/quadro horário.
 */
@Service
@RequiredArgsConstructor
public class ValidarCopiarQuadroHorarioUseCase {

    private final DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    /**
     * Valida as regras de negócio para a cópia de uma lista de alocações.
     * Realiza verificações de integridade dos dados, compatibilidade de salas
     * e conformidade com a disponibilidade dos professores em lote para melhor desempenho.
     *
     * @param alocacoes Lista de alocações a serem validadas.
     * @throws BusinessException se alguma regra de negócio for violada ou se houver dados incompletos.
     */
    @Transactional(readOnly = true)
    public void validarRegrasParaCopiaDeGrade(List<Alocacao> alocacoes) {

        // 1. Validação de segurança: Não é possível copiar uma grade vazia (sem alocações)
        if (alocacoes.isEmpty()) {
            throw new BusinessException(
                    "Não é possível copiar um quadro horário que não possui alocações.");
        }

        // 2. Otimização de Performance (Evita N+1 Queries):
        // Mapeia todos os IDs únicos dos professores envolvidos nas alocações a serem copiadas
        List<Long> professorIds = alocacoes.stream()
                .map(a -> a.getProfessor().getId())
                .distinct()
                .toList();

        // Busca no banco de dados, em uma única consulta, a disponibilidade de todos esses professores
        List<DisponibilidadeProfessor> disponibilidades = disponibilidadeProfessorRepository
                .findByProfessorIdIn(professorIds);

        // Converte a lista de disponibilidades em um Set de chaves (DisponibilidadeKey) para permitir
        // buscas rápidas com complexidade O(1) durante a iteração
        Set<DisponibilidadeKey> disponibilidadesSet = disponibilidades.stream()
                .map(d -> new DisponibilidadeKey(
                        d.getProfessor().getId(),
                        d.getDiaSemana().getId(),
                        d.getBlocoHorario().getId()))
                .collect(Collectors.toSet());

        // 3. Iteração e validação individual de cada alocação da grade de origem
        for (Alocacao antiga : alocacoes) {

            // Validação de Integridade: Garante que todos os relacionamentos obrigatórios da alocação estão presentes
            if (antiga.getDisciplina() == null
                    || antiga.getProfessor() == null
                    || antiga.getSala() == null
                    || antiga.getDiaSemana() == null
                    || antiga.getBlocoHorario() == null) {

                throw new BusinessException(
                        "Dados de alocação de origem corrompidos ou incompletos.");
            }
            
            // Validação de Disponibilidade do Professor:
            // Verifica se o professor possui disponibilidade cadastrada para o dia da semana e bloco de horário específicos
            boolean disponivel = disponibilidadesSet.contains(
                    new DisponibilidadeKey(
                            antiga.getProfessor().getId(),
                            antiga.getDiaSemana().getId(),
                            antiga.getBlocoHorario().getId()));

            if (!disponivel) {
                throw new BusinessException(
                        "Professor sem disponibilidade para o horário copiado.");
            }

            // Validação de Compatibilidade de Sala:
            // Garante que o tipo da sala alocada é compatível com o tipo de sala exigido pela disciplina
            boolean tipoCompativel = antiga.getDisciplina().getTipoSala().getId()
                    .equals(antiga.getSala().getTipoSala().getId());

            if (!tipoCompativel) {
                throw new BusinessException(
                        "O tipo de sala não atende aos requisitos da disciplina.");
            }
        }
    }
}