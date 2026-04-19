package com.fatec.horario.web.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.fatec.horario.domain.services.HistoricoAlteracaoService;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoRequest;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/historicos-alteracoes")
@CrossOrigin
public class HistoricoAlteracaoController {

	@Autowired
	private HistoricoAlteracaoService service;

	@PostMapping
	public ResponseEntity<HistoricoAlteracaoResponse> criar(
			@Valid @RequestBody HistoricoAlteracaoRequest request) {

		HistoricoAlteracaoResponse response = service.criar(request);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();

		return ResponseEntity.created(location).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<HistoricoAlteracaoResponse> atualizar(
			@PathVariable Long id,
			@Valid @RequestBody HistoricoAlteracaoRequest request) {

		return ResponseEntity.ok(service.atualizar(id, request));
	}

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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {

		service.deletar(id);

		return ResponseEntity.noContent().build();
	}

}
