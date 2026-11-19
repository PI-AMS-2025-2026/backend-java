package com.fatec.gini.web.exception;

public class AlocacaoLoteException extends RuntimeException {
    private final int indice;

    public AlocacaoLoteException(int indice, RuntimeException cause) {
        super(cause.getMessage(), cause);
        this.indice = indice;
    }

    public int getIndice() {
        return indice;
    }
}
