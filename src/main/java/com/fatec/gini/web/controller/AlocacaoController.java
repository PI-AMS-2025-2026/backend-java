package com.fatec.gini.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.services.AlocacaoService;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;

import jakarta.validation.Valid;

/**
 * Camada de Controle: Responsável por expor os endpoints da API e
 * tratar as requisições HTTP (entrada e saída de dados).
 */
@RestController
@RequestMapping("/alocacoes")
@CrossOrigin
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
            @RequestParam(required = false) Long grade,
          @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Encaminha os IDs para a camada de serviço
        return ResponseEntity.ok(service.listar(turma, disciplina, sala, usuario, diaSemana, horario, grade, page, size));
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