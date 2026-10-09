package com.fatec.gini.dto.alocacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fatec.gini.domain.entities.DiaSemana;
import org.junit.jupiter.api.Test;

class DiaSemanaContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Test
    void deveDesserializarDiaTextualCanonico() throws Exception {
        AlocacaoRequest request = objectMapper.readValue(
                "{\"dia_semana\":\"SÁBADO\"}",
                AlocacaoRequest.class);

        assertThat(request.diaSemana()).isEqualTo(DiaSemana.SÁBADO);
    }

    @Test
    void deveRejeitarDiaNumerico() {
        assertThatThrownBy(() -> objectMapper.readValue(
                "{\"dia_semana\":1}",
                AlocacaoRequest.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void deveManterOrdemLogicaDosDias() {
        assertThat(DiaSemana.SÁBADO.anterior()).isEqualTo(DiaSemana.SEXTA);
        assertThat(DiaSemana.SÁBADO.posterior()).isEqualTo(DiaSemana.DOMINGO);
        assertThat(DiaSemana.DOMINGO.posterior()).isEqualTo(DiaSemana.SEGUNDA);
    }
}
