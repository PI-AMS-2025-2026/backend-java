package com.fatec.horario.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.services.CursoServico;
import com.fatec.horario.dto.curso.CursoRequisicao;
import com.fatec.horario.dto.curso.CursoResposta;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
@CrossOrigin
public class CursoController {

    @Autowired
    private CursoServico servico;

    @GetMapping
    public ResponseEntity<List<CursoResposta>> listarTodos() {

        List<CursoResposta> cursos = servico.listarTodos();

        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResposta> buscarPorId(@PathVariable Long id) {

        CursoResposta curso = servico.buscarPorId(id);

        return ResponseEntity.ok(curso);
    }

    @PostMapping
    public ResponseEntity<CursoResposta> criar(@Valid @RequestBody CursoRequisicao requisicao) {

        CursoResposta curso = servico.criar(requisicao);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(curso.id())
                .toUri();

        return ResponseEntity.created(location).body(curso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResposta> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequisicao requisicao) {

        CursoResposta curso = servico.atualizar(id, requisicao);

        return ResponseEntity.ok(curso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        servico.deletar(id);

        return ResponseEntity.noContent().build();
    }
}