DO $assertions$
DECLARE
    quadros_vinculados INTEGER;
    alocacoes_corretas INTEGER;
    historico_preservado INTEGER;
    legados_arquivados INTEGER;
    versoes_preservadas INTEGER;
    indice_unico_presente INTEGER;
BEGIN
    SELECT COUNT(*) INTO quadros_vinculados
    FROM quadro_horario
    WHERE id_turma IS NOT NULL
      AND id_curso = 100
      AND id_periodo_atividade_quadro = 100;

    IF quadros_vinculados <> 3 THEN
        RAISE EXCEPTION 'Esperados 3 quadros por turma, encontrados %', quadros_vinculados;
    END IF;

    SELECT COUNT(*) INTO alocacoes_corretas
    FROM alocacao a
    JOIN quadro_horario q ON q.id_quadro_horario = a.id_quadro_horario
    WHERE a.id_alocacao IN (200, 201, 202)
      AND q.id_turma = a.id_turma
      AND q.id_quadro_horario <> 100
      AND q.id_quadro_horario <> 101;

    IF alocacoes_corretas <> 3 THEN
        RAISE EXCEPTION 'As 3 alocações devem apontar para o quadro da própria turma; encontradas %', alocacoes_corretas;
    END IF;

    SELECT COUNT(*) INTO historico_preservado
    FROM historico_versao_alocacao h
    JOIN alocacao a ON a.id_alocacao = h.id_alocacao
    WHERE h.id_historico_versao_alocacaoo = 200
      AND h.id_alocacao = 201
      AND a.id_quadro_horario <> 101;

    IF historico_preservado <> 1 THEN
        RAISE EXCEPTION 'Histórico da alocação 201 não foi preservado';
    END IF;

    SELECT COUNT(*) INTO legados_arquivados
    FROM quadro_horario
    WHERE id_quadro_horario IN (100, 101, 102)
      AND id_turma IS NULL
      AND status = 'INATIVO';

    IF legados_arquivados <> 3 THEN
        RAISE EXCEPTION 'Os quadros originais, incluindo o vazio, devem ser arquivados';
    END IF;

    SELECT COUNT(*) INTO versoes_preservadas
    FROM quadro_horario q
    JOIN periodo_atividade_quadro p
      ON p.id_periodo_atividade_quadro = q.id_periodo_atividade_quadro
    WHERE q.id_turma IS NOT NULL
      AND q.id_curso = 100
      AND q.id_periodo_atividade_quadro = 100
      AND q.data_criacao IN ('2026-01-02 08:00:00', '2026-02-02 08:00:00')
      AND q.versao IN (1, 2);

    IF versoes_preservadas <> 3 THEN
        RAISE EXCEPTION 'Versões e datas dos quadros clonados não foram preservadas';
    END IF;

    SELECT COUNT(*) INTO indice_unico_presente
    FROM pg_indexes
    WHERE schemaname = current_schema()
      AND indexname = 'uk_quadro_horario_ativo_turma_periodo';

    IF indice_unico_presente <> 1 THEN
        RAISE EXCEPTION 'Índice único de quadro ativo por turma e período não foi criado';
    END IF;

    BEGIN
        INSERT INTO quadro_horario (
            versao, data_criacao, status, id_curso, id_turma,
            id_periodo_atividade_quadro
        )
        VALUES (99, CURRENT_TIMESTAMP, 'ATIVO', 100, 100, 100);

        RAISE EXCEPTION 'O banco aceitou dois quadros ativos para a mesma turma e período';
    EXCEPTION
        WHEN unique_violation THEN
            NULL;
    END;
END
$assertions$;
