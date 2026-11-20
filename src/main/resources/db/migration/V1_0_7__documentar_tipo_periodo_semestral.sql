-- A coluna armazena o ordinal, não o texto do enum Java. A correção
-- SEMENSTRAL -> SEMESTRAL preserva o valor 0 e ANUAL permanece 1;
-- nenhum registro precisa ser alterado.
COMMENT ON COLUMN periodo_atividade_quadro.tipo_periodo_atividade_quadro
    IS 'Tipo de período: 0 = SEMESTRAL; 1 = ANUAL (ordinal do enum).';
