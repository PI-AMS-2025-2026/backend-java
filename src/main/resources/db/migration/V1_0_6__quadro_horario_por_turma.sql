-- Cada nova versão do quadro passa a pertencer a uma turma.
-- A coluna permanece nullable para preservar quadros legados sem alocações,
-- que não possuem evidência suficiente para associação a uma turma.
ALTER TABLE quadro_horario
    ADD COLUMN id_turma BIGINT REFERENCES turma(id_turma);

CREATE INDEX ix_quadro_horario_turma_periodo_versao
    ON quadro_horario (id_turma, id_periodo_atividade_quadro, versao DESC);

-- Dados inconsistentes não podem ser atribuídos silenciosamente a um quadro
-- de turma de outro curso. A exceção interrompe a migration de forma atômica.
DO $validation$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM alocacao a
        JOIN quadro_horario q ON q.id_quadro_horario = a.id_quadro_horario
        JOIN turma t ON t.id_turma = a.id_turma
        WHERE q.id_curso <> t.id_curso
    ) THEN
        RAISE EXCEPTION 'Há alocações cujo quadro e turma pertencem a cursos diferentes; corrija os dados antes da V1_0_6.';
    END IF;
END
$validation$;

-- Registra as relações comprovadas pelas alocações existentes. Um quadro
-- compartilhado gera uma cópia por turma; as alocações conservarão seus IDs.
CREATE TEMPORARY TABLE quadro_horario_turma_migracao (
    quadro_original_id BIGINT NOT NULL,
    turma_id BIGINT NOT NULL,
    quadro_turma_id BIGINT,
    PRIMARY KEY (quadro_original_id, turma_id)
) ON COMMIT DROP;

INSERT INTO quadro_horario_turma_migracao (quadro_original_id, turma_id)
SELECT DISTINCT id_quadro_horario, id_turma
FROM alocacao;

DO $migration$
DECLARE
    associacao RECORD;
    novo_quadro_id BIGINT;
BEGIN
    FOR associacao IN
        SELECT
            q.versao,
            q.data_criacao,
            q.status,
            q.id_curso,
            q.id_periodo_atividade_quadro,
            q.created_at,
            q.updated_at,
            m.quadro_original_id,
            m.turma_id
        FROM quadro_horario_turma_migracao m
        JOIN quadro_horario q ON q.id_quadro_horario = m.quadro_original_id
        ORDER BY m.quadro_original_id, m.turma_id
    LOOP
        INSERT INTO quadro_horario (
            versao,
            data_criacao,
            status,
            id_curso,
            id_turma,
            id_periodo_atividade_quadro,
            created_at,
            updated_at
        ) VALUES (
            associacao.versao,
            associacao.data_criacao,
            associacao.status,
            associacao.id_curso,
            associacao.turma_id,
            associacao.id_periodo_atividade_quadro,
            associacao.created_at,
            associacao.updated_at
        )
        RETURNING id_quadro_horario INTO novo_quadro_id;

        UPDATE quadro_horario_turma_migracao
        SET quadro_turma_id = novo_quadro_id
        WHERE quadro_original_id = associacao.quadro_original_id
          AND turma_id = associacao.turma_id;
    END LOOP;
END
$migration$;

-- Reaponta cada alocação para a cópia da sua própria turma, sem alterar seu ID
-- nem o vínculo de histórico que referencia esse ID.
UPDATE alocacao a
SET id_quadro_horario = m.quadro_turma_id
FROM quadro_horario_turma_migracao m
WHERE a.id_quadro_horario = m.quadro_original_id
  AND a.id_turma = m.turma_id;

-- Os quadros originais ficam preservados como legado e não participam do novo
-- fluxo de quadros por turma. Inclui os quadros que já estavam vazios.
UPDATE quadro_horario
SET status = 'INATIVO',
    updated_at = CURRENT_TIMESTAMP
WHERE id_turma IS NULL;

-- Mantém apenas uma versão ativa para cada turma no período letivo.
CREATE UNIQUE INDEX uk_quadro_horario_ativo_turma_periodo
    ON quadro_horario (id_turma, id_periodo_atividade_quadro)
    WHERE status = 'ATIVO' AND id_turma IS NOT NULL;
