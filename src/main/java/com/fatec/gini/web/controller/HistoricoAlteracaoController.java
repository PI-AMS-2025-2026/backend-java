package com.fatec.gini.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.services.HistoricoAlteracaoService;
import com.fatec.gini.dto.historicoAlteracao.HistoricoAlteracaoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/historicos-alteracoes")
@RequiredArgsConstructor
public class HistoricoAlteracaoController {

	private final HistoricoAlteracaoService service;

	@GetMapping
	public ResponseEntity<Page<HistoricoAlteracaoResponse>> listar(
			@RequestParam(required = false, name = "alocacao") Long idAlocacao,
			@RequestParam(required = false, name = "usuario") Long idUsuario,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(service.listar(idAlocacao, idUsuario, page, size));
	}

	@GetMapping("/{id}")
	public ResponseEntity<HistoricoAlteracaoResponse> buscarPorId(@PathVariable Long id) {

		return ResponseEntity.ok(service.buscarPorId(id));
	}

}
