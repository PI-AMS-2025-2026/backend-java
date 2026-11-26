package com.fatec.gini.web.exception;

public class SalaComAlocacoesException extends RuntimeException {
    private final long quantidade;

    public SalaComAlocacoesException(long quantidade) {
        super("Esta sala possui " + quantidade + " alocação(ões). Confirme se deseja excluí-las junto com a sala.");
        this.quantidade = quantidade;
    }

    public long getQuantidade() {
        return quantidade;
    }
}
