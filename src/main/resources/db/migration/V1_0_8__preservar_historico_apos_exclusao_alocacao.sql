ALTER TABLE historico_versao_alocacao
    ADD COLUMN IF NOT EXISTS id_alocacao_origem BIGINT,
    ADD COLUMN IF NOT EXISTS id_curso BIGINT;

UPDATE historico_versao_alocacao h
SET id_alocacao_origem = h.id_alocacao,
    id_curso = q.id_curso
FROM alocacao a
JOIN quadro_horario q ON q.id_quadro_horario = a.id_quadro_horario
WHERE h.id_alocacao = a.id_alocacao
  AND (h.id_alocacao_origem IS NULL OR h.id_curso IS NULL);

ALTER TABLE historico_versao_alocacao
    ALTER COLUMN id_alocacao DROP NOT NULL,
    ALTER COLUMN id_alocacao_origem SET NOT NULL,
    ALTER COLUMN id_curso SET NOT NULL,
    DROP CONSTRAINT IF EXISTS fk_historico_versao_alocacao_curso;
