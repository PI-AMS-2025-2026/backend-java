package com.fatec.gini.web.exception;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

public class StandardError {
    private Instant timeStamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private Integer alocacaoIndice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getAlocacaoIndice() {
        return alocacaoIndice;
    }

    public void setAlocacaoIndice(Integer alocacaoIndice) {
        this.alocacaoIndice = alocacaoIndice;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
