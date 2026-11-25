-- Mantém o gerador de IDs alinhado a registros existentes, inclusive dados
-- restaurados/importados com IDs explícitos.
SELECT setval(
    pg_get_serial_sequence('periodo_atividade_quadro', 'id_periodo_atividade_quadro'),
    COALESCE(MAX(id_periodo_atividade_quadro), 1),
    MAX(id_periodo_atividade_quadro) IS NOT NULL
)
FROM periodo_atividade_quadro;
