ALTER TABLE turma
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO'
        CHECK (status IN ('ATIVO', 'INATIVO'));

ALTER TABLE historico_versao_alocacao
    DROP COLUMN justificativa;
