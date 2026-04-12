package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.AlocacaoService;
import com.fatec.horario.dto.Alocacao.AlocacaoRequest;
import com.fatec.horario.dto.Alocacao.AlocacaoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Camada de Controle: Responsável por expor os endpoints da API e
 * tratar as requisições HTTP (entrada e saída de dados).
 */
@RestController
@RequestMapping("/alocacoes")
public class AlocacaoController {

    @Autowired
    private AlocacaoService service;

    /**
     * Recebe um JSON (Request) e cria uma nova alocação.
     * Valid garante que as regras do DTO (como @NotNull) sejam checadas.
     */
    @PostMapping
    public ResponseEntity<AlocacaoResponse> criar(@RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    /**
     * Realiza a listagem com suporte a filtros opcionais e paginação.
     */
    @GetMapping
    public ResponseEntity<Page<AlocacaoResponse>> listar(
            @RequestParam(required = false) Long turma,
            @RequestParam(required = false) Long disciplina,
            @RequestParam(required = false) Long sala,
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false, name = "dia_semana") Long diaSemana,
            @RequestParam(required = false) Long horario,
            @RequestParam(required = false, name = "grade") Long grade,
            Pageable pageable) {

        // Encaminha os IDs para a camada de serviço
        return ResponseEntity.ok(service.listar(turma, disciplina, sala, usuario, diaSemana, horario, grade, pageable));
    }

    /**
     * Busca um registro específico pelo seu identificador único.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Atualiza os dados de uma alocação existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    /**
     * Remove um registro e retorna o status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}