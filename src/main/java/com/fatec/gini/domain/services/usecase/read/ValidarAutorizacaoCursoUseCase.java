package com.fatec.gini.domain.services.usecase.read;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.TipoUsuario;

@Service
public class ValidarAutorizacaoCursoUseCase {

    @Value("${app.security.authorization.enabled:true}")
    private boolean authorizationEnabled;

    public Usuario usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário autenticado não encontrado");
        }

        return usuario;
    }

    public void validarCurso(Long cursoId) {
        if (!authorizationEnabled) {
            return;
        }

        Usuario usuario = usuarioAutenticado();

        if (usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            return;
        }

        if (usuario.getTipoUsuario() != TipoUsuario.COORDENADOR
                || usuario.getCurso() == null
                || cursoId == null
                || !cursoId.equals(usuario.getCurso().getId())) {
            throw new AccessDeniedException("Usuário não possui acesso a este curso");
        }
    }

    public void validarAdministrador() {
        if (!authorizationEnabled) {
            return;
        }

        if (usuarioAutenticado().getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Somente administradores podem realizar esta operação");
        }
    }

    public void validarCurso(Curso curso) {
        validarCurso(curso == null ? null : curso.getId());
    }

    public Long cursoParaFiltro(Long cursoSolicitado) {
        if (!authorizationEnabled) {
            return cursoSolicitado;
        }

        Usuario usuario = usuarioAutenticado();
        if (usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            return cursoSolicitado;
        }
        validarCurso(usuario.getCurso());
        return usuario.getCurso().getId();
    }

    public void validarCursosIguais(Curso primeiro, Curso segundo) {
        if (!authorizationEnabled) {
            return;
        }

        if (primeiro == null || segundo == null || !primeiro.getId().equals(segundo.getId())) {
            throw new AccessDeniedException("As entidades devem pertencer ao mesmo curso");
        }
        validarCurso(primeiro);
    }
}