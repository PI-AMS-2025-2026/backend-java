package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.curso.CursoRequest;
import jakarta.persistence.EntityManager;

@SpringBootTest(properties = "app.security.authorization.enabled=false")
@ActiveProfiles("test")
@Transactional
class CursoCatalogoIntegracaoTest {
    @Autowired CursoService service;
    @Autowired EntityManager entityManager;

    @Test
    void persisteEditaListaPaginadaERejeitaDuplicidade() {
        var original = service.criar(new CursoRequest("Curso Teste Catalogo", "Semestral", Status.ATIVO, 6));
        var outraDuracao = service.criar(new CursoRequest("Curso Teste Catalogo", "Semestral", Status.ATIVO, 8));
        entityManager.flush(); entityManager.clear();
        assertThat(service.buscarPorId(original.id()).nome()).isEqualTo("Curso Teste Catalogo");
        assertThatThrownBy(() -> service.criar(new CursoRequest("curso teste catalogo", "semestral", Status.ATIVO, 6)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Já existe");
        assertThatThrownBy(() -> service.atualizar(outraDuracao.id(), new CursoRequest("CURSO TESTE CATALOGO", "Semestral", Status.ATIVO, 6)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Já existe");
        service.atualizar(original.id(), new CursoRequest("Curso Teste Editado", "Semestral", Status.ATIVO, 6));
        entityManager.flush(); entityManager.clear();
        assertThat(service.buscarPorId(original.id()).nome()).isEqualTo("Curso Teste Editado");
        var primeira = service.listar(null, null, null, null, "Curso Teste", 0, 1);
        var segunda = service.listar(null, null, null, null, "Curso Teste", 1, 1);
        assertThat(primeira.totalElements()).isEqualTo(2);
        assertThat(primeira.content().get(0).id()).isNotEqualTo(segunda.content().get(0).id());
    }
}
