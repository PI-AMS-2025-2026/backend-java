package com.fatec.gini.web.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
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

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.QuadroHorarioService;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Quadro Horário")
@RestController
@RequestMapping("/quadro-horarios")
@RequiredArgsConstructor
public class QuadroHorarioController {

        private final QuadroHorarioService service;

        @GetMapping
        public ResponseEntity<PageResponse<QuadroHorarioResponse>> listar(
                        @RequestParam(name = "curso", required = false) Long idCurso,

                        @RequestParam(name = "periodo_atividade_quadro", required = false) Long idPeriodoAtividadeQuadro,

                        @RequestParam(required = false) Status status,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                service.listar(
                                                idCurso,
                                                idPeriodoAtividadeQuadro,
                                                status,
                                                page,
                                                size));
        }

        @GetMapping("/{id}")
        public ResponseEntity<QuadroHorarioResponse> buscar(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                service.buscarPorId(id));
        }

        @PostMapping
        public ResponseEntity<QuadroHorarioResponse> criar(
                        @Valid @RequestBody QuadroHorarioRequest request) {

                QuadroHorarioResponse response = service.criar(request);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.id())
                                .toUri();

                return ResponseEntity
                                .created(location)
                                .body(response);
        }

        @PostMapping("/{id}/copiar")
        public ResponseEntity<QuadroHorarioResponse> copiar(
                        @PathVariable Long id,

                        @Valid @RequestBody QuadroHorarioRequest request) {

                QuadroHorarioResponse response = service.copiar(
                                id,
                                request);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(response.id())
                                .toUri();

                return ResponseEntity
                                .created(location)
                                .body(response);
        }

        @GetMapping("/cursos/{id}")
        @Operation(summary = "Consultar grade horária completa do curso")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Grade do curso retornada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Curso ou quadro horário ativo não encontrado")
        })
        public ResponseEntity<GradeCursoResponse> getMethodName(@PathVariable Long id) {
        
                var response = service.obterGradeCurso(id);
                return ResponseEntity.ok(response);
        }

   /* @GetMapping("/turmas/{turmaId}")
    @Operation(summary = "Consultar horários de uma turma do curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários da turma retornados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui acesso ao curso"),
            @ApiResponse(responseCode = "404", description = "Curso, turma ou quadro horário ativo não encontrado")
    })
    public ResponseEntity<GradeTurmaResponse> obterGradeTurma(
            @PathVariable Long cursoId,
            @PathVariable Long turmaId) {
        GradeTurmaResponse response = service.obterGradeTurma(cursoId, turmaId);
        return ResponseEntity.ok(response);
    }*/


        @PutMapping("/{id}")
        public ResponseEntity<QuadroHorarioResponse> atualizar(
                        @PathVariable Long id,

                        @Valid @RequestBody QuadroHorarioRequest request) {

                return ResponseEntity.ok(
                                service.atualizar(
                                                id,
                                                request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(
                        @PathVariable Long id) {

                service.inativar(id);

                return ResponseEntity.noContent().build();
        }
}