package com.fatec.horario.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

// Ativa o Mockito dentro do JUnit 5 — sem isso, @Mock e @InjectMocks abaixo não funcionam
@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    // Cria uma versão "fingida" do CursoRepository.
    // Ela não acessa banco nenhum: por padrão, todo método retorna null/vazio
    // até a gente dizer explicitamente o que ele deve fazer com when(...)
    @Mock
    private CursoRepository repository;

    // Cria uma instância REAL do CursoService e injeta o repository mockado
    // dentro dela automaticamente (o Mockito encontra o campo @Autowired
    // dentro do CursoService e encaixa o mock ali)
    @InjectMocks
    private CursoService cursoService;

    @Test
    void deveCriarCursoComDadosValidos() {
        // ---------- Arrange (organizar o cenário) ----------

        // Simula o dado que chegaria do front-end/controller
        CursoRequest request = new CursoRequest("ADS", "Semestral", Status.ATIVO, 6);

        // Simula como o curso ficaria DEPOIS de salvo no banco (já com ID gerado)
        Curso cursoSalvo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoSalvo.setId(1L);

        // Ensina o mock: "quando o método save() for chamado com qualquer
        // objeto Curso, finja que o banco devolveu cursoSalvo"
        when(repository.save(any(Curso.class))).thenReturn(cursoSalvo);

        // ---------- Act (executar o método real que queremos testar) ----------
        CursoResponse resultado = cursoService.criar(request);

        // ---------- Assert (verificar se o resultado é o esperado) ----------
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("ADS");
        assertThat(resultado.status()).isEqualTo(Status.ATIVO);
    }

    @Test
    void deveLancarExcecaoQuandoCursoNaoExiste() {
        // ---------- Arrange ----------

        // findById() do Spring Data JPA sempre retorna um Optional<Curso>,
        // porque o registro pode existir ou não.
        // Aqui simulamos o caso em que NÃO existe: Optional.empty()
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ---------- Act + Assert (juntos, porque estamos testando uma exceção) ----------

        // assertThrows executa a função lambda por dentro e verifica se ela
        // lança a exceção esperada. Não dá pra guardar "resultado" numa variável
        // aqui, porque o método nunca termina normalmente quando dá erro.
        assertThrows(EntityNotFoundException.class, () -> cursoService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarCursoExistente() {
        // ---------- Arrange ----------

        // Simula o curso como ele está ANTES da atualização (o que o
        // repositório "encontra" no banco)
        Curso cursoExistente = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoExistente.setId(1L);

        // Simula os NOVOS dados que o usuário está enviando para atualizar
        CursoRequest requestAtualizado = new CursoRequest("Engenharia de Software", "Anual", Status.ATIVO, 8);

        // Quando o service buscar o curso pelo ID 1, finge que encontrou o cursoExistente
        when(repository.findById(1L)).thenReturn(Optional.of(cursoExistente));

        // Quando o service salvar (depois de alterar os campos), devolve o
        // próprio objeto que foi passado para save() — simulando que o banco
        // "aceitou" a atualização sem mudar mais nada
        when(repository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ---------- Act ----------
        CursoResponse resultado = cursoService.atualizar(1L, requestAtualizado);

        // ---------- Assert ----------

        // Confere se os campos realmente foram sobrescritos com os novos valores
        assertThat(resultado.nome()).isEqualTo("Engenharia de Software");
        assertThat(resultado.periodicidade()).isEqualTo("Anual");
        assertThat(resultado.duracao()).isEqualTo(8);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCursoInexistente() {
        // ---------- Arrange ----------

        CursoRequest requestAtualizado = new CursoRequest("Qualquer Nome", "Anual", Status.ATIVO, 8);

        // Simula que o curso com ID 99 não existe no banco
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ---------- Act + Assert ----------

        // O atualizar() busca o curso ANTES de alterar qualquer campo — então,
        // se ele não existe, a exceção precisa ser lançada logo aqui
        assertThrows(EntityNotFoundException.class, () -> cursoService.atualizar(99L, requestAtualizado));
    }

    @Test
    void deveInativarCursoAlterandoStatusSemExcluir() {
        // ---------- Arrange ----------

        // Curso "encontrado" no banco, ainda com status ATIVO
        Curso cursoExistente = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoExistente.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(cursoExistente));

        // ---------- Act ----------
        cursoService.inativar(1L);

        // ---------- Assert ----------

        // O método inativar() não retorna nada (void), então não temos um
        // "resultado" pra conferir. Em vez disso, verificamos o efeito colateral:
        // o próprio objeto cursoExistente deve ter sido alterado em memória,
        // já que Curso é um objeto mutável (tem setters).
        assertThat(cursoExistente.getStatus()).isEqualTo(Status.INATIVO);
    }

    @Test
    void deveListarCursosComFiltrosAplicados() {
        // ---------- Arrange ----------

        // Monta uma lista de cursos "encontrados no banco" — o resultado que
        // o repositório fingido vai devolver
        Curso curso1 = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso1.setId(1L);
        Curso curso2 = new Curso("Engenharia de Software", "Semestral", Status.ATIVO, 6);
        curso2.setId(2L);

        // Page é o "envelope" que o Spring Data JPA usa para representar uma
        // página de resultados (junto com informações de paginação).
        // PageImpl é a implementação concreta que usamos para MONTAR uma Page
        // manualmente em testes.
        Page<Curso> paginaSimulada = new PageImpl<>(List.of(curso1, curso2));

        // any() sem tipo específico é usado aqui porque os filtros (nome,
        // periodicidade, status, duracao) podem vir como null — o any()
        // aceita qualquer valor, incluindo null, para cada argumento
        when(repository.buscarPorFiltros(any(), any(), any(), any(), any(PageRequest.class)))
                .thenReturn(paginaSimulada);

        // ---------- Act ----------
        Page<CursoResponse> resultado = cursoService.listar(null, null, null, null, 0, 10);

        // ---------- Assert ----------

        // Confere se a página tem a quantidade certa de itens
        assertThat(resultado.getContent()).hasSize(2);

        // Confere se os dados foram convertidos corretamente de Curso para CursoResponse
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("ADS");
        assertThat(resultado.getContent().get(1).nome()).isEqualTo("Engenharia de Software");
    }
}