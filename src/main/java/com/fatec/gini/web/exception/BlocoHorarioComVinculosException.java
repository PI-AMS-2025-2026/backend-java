package com.fatec.gini.web.exception;

public class BlocoHorarioComVinculosException extends RuntimeException {
    private final long alocacoes;
    private final long disponibilidades;

    public BlocoHorarioComVinculosException(long alocacoes, long disponibilidades) {
        super("Este bloco de horário possui " + alocacoes + " alocações e " + disponibilidades
                + " disponibilidades vinculadas. Confirme para excluir esses vínculos.");
        this.alocacoes = alocacoes;
        this.disponibilidades = disponibilidades;
    }

    public long getAlocacoes() { return alocacoes; }
    public long getDisponibilidades() { return disponibilidades; }
}
