DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'alocacao'
          AND column_name = 'dia_semana'
          AND data_type = 'smallint'
    ) THEN
        ALTER TABLE alocacao
            ALTER COLUMN dia_semana TYPE VARCHAR(20)
            USING CASE dia_semana
                WHEN 0 THEN 'SEGUNDA'
                WHEN 1 THEN 'TERÇA'
                WHEN 2 THEN 'QUARTA'
                WHEN 3 THEN 'QUINTA'
                WHEN 4 THEN 'SEXTA'
                WHEN 5 THEN 'SÁBADO'
                WHEN 6 THEN 'DOMINGO'
            END;
    END IF;
END $$;

ALTER TABLE alocacao
    DROP CONSTRAINT IF EXISTS alocacao_dia_semana_check;

ALTER TABLE alocacao
    ADD CONSTRAINT alocacao_dia_semana_check
    CHECK (dia_semana IN ('SEGUNDA', 'TERÇA', 'QUARTA', 'QUINTA', 'SEXTA', 'SÁBADO', 'DOMINGO'));
