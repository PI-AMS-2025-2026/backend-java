package com.fatec.gini.web.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class ResourceExceptionHandlerTest {

    private final ResourceExceptionHandler handler = new ResourceExceptionHandler();

    @Test
    void informaFalhaDeIdentificadorComoErroInternoEOrientadoAoUsuario() {
        var request = request();
        var exception = integrityViolation(
                "ERROR: duplicate key value violates unique constraint \"periodo_atividade_quadro_pkey\"");

        var response = handler.dataIntegrityViolationException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("gerar um identificador");
        assertThat(response.getBody().getMessage()).doesNotContain("SQLState", "duplicate key");
    }

    @Test
    void mantemMensagemEspecificaParaPeriodoDuplicado() {
        var exception = integrityViolation(
                "ERROR: duplicate key value violates unique constraint \"uk_periodo_atividade_quadro_ano_periodo_tipo\"");

        var response = handler.dataIntegrityViolationException(exception, request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Já existe um período atividade quadro");
    }

    @Test
    void reservaMensagemDeVinculosParaViolacaoDeChaveEstrangeira() {
        var exception = integrityViolation("ERROR: violates foreign key constraint \"fk_exemplo\"");

        var response = handler.dataIntegrityViolationException(exception, request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("existem registros relacionados");
    }

    @Test
    void naoApresentaErroDeVinculoParaViolacaoDesconhecida() {
        var exception = integrityViolation("ERROR: check constraint \"ck_exemplo\" failed");

        var response = handler.dataIntegrityViolationException(exception, request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("inconsistência nos dados");
        assertThat(response.getBody().getMessage()).doesNotContain("registros relacionados");
    }

    private MockHttpServletRequest request() {
        var request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/periodos-atividade-quadro");
        return request;
    }

    private DataIntegrityViolationException integrityViolation(String message) {
        return new DataIntegrityViolationException("Falha de integridade", new SQLException(message, "23505"));
    }
}
