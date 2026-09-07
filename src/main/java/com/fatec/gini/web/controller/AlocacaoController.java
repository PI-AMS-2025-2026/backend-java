package com.fatec.gini.web.controller;

import java.net.URI;
import java.util.List;


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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.services.AlocacaoService;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoRequest;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Camada de controle responsável por expor
 * os endpoints relacionados às alocações.
 */
@Tag(name = "Alocações")
@RestController
@RequestMapping("/alocacoes")
@RequiredArgsConstructor
@CrossOrigin
public class AlocacaoController {

        private final AlocacaoService service;

        /**
         * Cria uma nova alocação.
         */
        @PostMapping
        public ResponseEntity<AlocacaoResponse> criar(@RequestBody @Valid AlocacaoRequest request) {
                var response = service.criar(request);
                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.id())
                                .toUri();

                return ResponseEntity.created(location).body(response);
        }

        /**
         * Cria alocações em lote.
         */
        @PostMapping("/lote")
        @Operation(summary = "Criar alocações em lote")
        public ResponseEntity<List<AlocacaoResponse>> criarLote(
                        @RequestBody @Valid List<@Valid AlocacaoRequest> requests) {

                List<AlocacaoResponse> response = service.criarLote(requests);

                return ResponseEntity.ok(response);
        }

        /**
         * RF06 — Sugestão Automática de Ajuste.
         */
        @PostMapping("/sugestoes")
        @Operation(summary = "Sugestão automática de ajuste (RF06)")
        public ResponseEntity<SugestaoResponse> sugerirAjuste(
                        @RequestBody @Valid SugestaoRequest request) {

                return ResponseEntity.ok(
                                service.sugerirAlternativas(request));
        }

        /**
         * Realiza a listagem com filtros opcionais e paginação.
         */
        @GetMapping
        @Operation(summary = "Listagem de alocações")
        public ResponseEntity<PageResponse<AlocacaoResponse>> listar(

                        @RequestParam(required = false) Long turma,

                        @RequestParam(required = false) Long disciplina,

                        @RequestParam(required = false) Long sala,

                        @RequestParam(required = false) Long usuario,

                        @RequestParam(required = false, name = "dia_semana") DiaSemana diaSemana,

                        @RequestParam(required = false) Long horario,

                        @RequestParam(required = false, name = "quadro_horario") Long quadroHorario,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                service.listar(
                                                turma,
                                                disciplina,
                                                sala,
                                                usuario,
                                                diaSemana,
                                                horario,
                                                quadroHorario,
                                                page,
                                                size));
        }

        /**
         * Busca uma alocação pelo ID.
         */
        @GetMapping("/{id}")
        public ResponseEntity<AlocacaoResponse> buscarPorId(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                service.buscarPorId(id));
        }

        /**
         * Atualiza uma alocação existente.
         */
        @PutMapping("/{id}")
        public ResponseEntity<AlocacaoResponse> atualizar(
                        @PathVariable Long id,
                        @RequestBody @Valid AlocacaoRequest request) {

                return ResponseEntity.ok(
                                service.atualizar(id, request));
        }

        /**
         * Remove uma alocação.
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(
                        @PathVariable Long id) {

                service.deletar(id);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}