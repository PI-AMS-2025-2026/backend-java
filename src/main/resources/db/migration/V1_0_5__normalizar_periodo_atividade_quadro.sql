-- O campo periodo era usado como duração em meses (6/12) e também como
-- posição no ano letivo (1/2). Padroniza os registros antigos para a segunda
-- interpretação e preenche o tipo armazenado como ordinal do enum:
-- SEMENSTRAL = 0 e ANUAL = 1.
UPDATE periodo_atividade_quadro
SET tipo_periodo_atividade_quadro = CASE
        WHEN periodo = 12 THEN 1
        WHEN periodo = 6 THEN 0
        WHEN tipo_periodo_atividade_quadro IS NULL THEN 0
        ELSE tipo_periodo_atividade_quadro
    END,
    periodo = CASE
        WHEN periodo = 12 THEN 1
        WHEN periodo = 6 AND EXTRACT(MONTH FROM data_inicio) <= 6 THEN 1
        WHEN periodo = 6 THEN 2
        ELSE periodo
    END
WHERE data_inicio IS NOT NULL
  AND periodo IN (1, 2, 6, 12);

ALTER TABLE periodo_atividade_quadro
    DROP CONSTRAINT IF EXISTS periodo_atividade_quadro_tipo_periodo_atividade_quadro_check;

ALTER TABLE periodo_atividade_quadro
    ADD CONSTRAINT chk_periodo_atividade_tipo
    CHECK (tipo_periodo_atividade_quadro IS NULL OR tipo_periodo_atividade_quadro BETWEEN 0 AND 1);


-- A identidade do período considera ano letivo, posição no ano e tipo.
-- Status e datas não fazem parte da identidade: inativar não libera duplicação.
ALTER TABLE periodo_atividade_quadro
    ALTER COLUMN ano SET NOT NULL,
    ALTER COLUMN periodo SET NOT NULL,
    ALTER COLUMN tipo_periodo_atividade_quadro SET NOT NULL;

ALTER TABLE periodo_atividade_quadro
    ADD CONSTRAINT uk_periodo_atividade_quadro_ano_periodo_tipo
    UNIQUE (ano, periodo, tipo_periodo_atividade_quadro);
