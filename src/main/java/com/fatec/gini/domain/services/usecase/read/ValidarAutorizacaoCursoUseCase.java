package com.fatec.gini.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.TipoUsuario;

/**
 * Caso de uso responsável por gerenciar e aplicar as regras de validação e autorização de acesso 
 * a cursos com base no perfil do usuário autenticado no sistema.
 */
@Service
public class ValidarAutorizacaoCursoUseCase {

    @Value("${app.security.authorization.enabled:true}")
    private boolean authorizationEnabled;

    /**
     * Obtém o usuário atualmente autenticado a partir do contexto de segurança do Spring Security.
     *
     * @return O objeto {@link Usuario} associado à sessão atual.
     * @throws AccessDeniedException Se não houver autenticação ativa ou se o principal não for uma instância de {@link Usuario}.
     */
    public Usuario usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário autenticado não encontrado");
        }

        return usuario;
    }

    /**
     * Valida se o usuário autenticado possui acesso ao curso especificado pelo seu identificador (ID).
     * Administradores têm acesso total, enquanto Coordenadores só podem acessar o curso ao qual estão vinculados.
     *
     * @param cursoId Identificador único do curso a ser validado.
     * @throws AccessDeniedException Se a autorização estiver ativada e o usuário não tiver permissão para acessar o curso.
     */
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

    /**
     * Valida se o usuário autenticado possui o perfil de Administrador.
     *
     * @throws AccessDeniedException Se a autorização estiver ativada e o usuário não for um Administrador.
     */
    public void validarAdministrador() {
        if (!authorizationEnabled) {
            return;
        }

        if (usuarioAutenticado().getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Somente administradores podem realizar esta operação");
        }
    }

    /**
     * Sobrecarga do método de validação que recebe a entidade {@link Curso} diretamente.
     * Extrai o ID do curso fornecido e realiza a validação de permissão de acesso.
     *
     * @param curso Objeto do curso a ser validado (pode ser {@code null}).
     * @throws AccessDeniedException Se a autorização estiver ativada e o usuário não tiver acesso ao curso fornecido.
     */
    public void validarCurso(Curso curso) {
        validarCurso(curso == null ? null : curso.getId());
    }

    /**
     * Determina o ID do curso a ser utilizado em filtros de consulta.
     * Se o usuário for Administrador, o curso solicitado é mantido. 
     * Se for Coordenador, força a consulta para o curso ao qual ele está vinculado.
     *
     * @param cursoSolicitado ID do curso originalmente informado na requisição/filtro.
     * @return O ID do curso permitido para ser utilizado na busca.
     * @throws AccessDeniedException Se o usuário não for Administrador e não possuir um curso associado com permissão válida.
     */
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

    /**
     * Valida se duas entidades de {@link Curso} pertencem ao mesmo curso e se o usuário autenticado
     * possui permissão de acesso a este curso.
     *
     * @param primeiro Primeira entidade de curso a ser comparada.
     * @param segundo  Segunda entidade de curso a ser comparada.
     * @throws AccessDeniedException Se algum dos cursos for {@code null}, se tiverem IDs diferentes,
     *                               ou se o usuário não tiver permissão para o curso validado.
     */
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