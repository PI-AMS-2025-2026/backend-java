package com.fatec.horario.controllers.exceptions;

import org.springframework.web.bind.annotation.*;

import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.domain.services.TipoSalaService;

import java.util.List;

@RestController
@RequestMapping("/tipo-sala")
public class TipoSalaController {

    private final TipoSalaService service;

    public TipoSalaController(TipoSalaService service) {
        this.service = service;
    }

    @PostMapping
    public TipoSala criar(@RequestBody TipoSala tipoSala) {
        return service.criar(tipoSala);
    }

    @GetMapping
    public List<TipoSala> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoSala buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TipoSala atualizar(@PathVariable Long id, @RequestBody TipoSala tipoSala) {
        return service.atualizar(id, tipoSala);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}