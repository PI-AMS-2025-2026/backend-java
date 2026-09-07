package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.web.exception.BusinessException;

class ValidarCursoObrigatorioUsuarioUseCaseTest {

    private final ValidarCursoObrigatorioUsuarioUseCase useCase = new ValidarCursoObrigatorioUsuarioUseCase();

    @Test
    void deveRejeitarCoordenadorSemCurso() {
        UsuarioRequest request = request(TipoUsuario.COORDENADOR, null);

        assertThrows(BusinessException.class, () -> useCase.executar(request));
    }

    @Test
    void devePermitirAdministradorSemCurso() {
        UsuarioRequest request = request(TipoUsuario.ADMINISTRADOR, null);

        assertDoesNotThrow(() -> useCase.executar(request));
    }

    private UsuarioRequest request(TipoUsuario tipoUsuario, com.fatec.gini.dto.id.LongDTO curso) {
        return new UsuarioRequest("Usuário", "usuario@teste.com", "123456", Status.ATIVO, tipoUsuario, curso);
    }
}