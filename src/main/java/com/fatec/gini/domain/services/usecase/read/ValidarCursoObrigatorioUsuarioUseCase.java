package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.web.exception.BusinessException;

@Service
public class ValidarCursoObrigatorioUsuarioUseCase {

    public void executar(UsuarioRequest request) {
        if (request.tipoUsuario() == TipoUsuario.COORDENADOR && request.curso() == null) {
            throw new BusinessException("Coordenador deve estar associado a um curso");
        }
    }
}