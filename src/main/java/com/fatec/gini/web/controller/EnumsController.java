package com.fatec.gini.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.TipoPeridoAtividadeQuadro;
import com.fatec.gini.domain.entities.TipoUsuario;

@RestController
public class EnumsController {

    @GetMapping("/dias-semana")
    public List<String> listar() {
        return Arrays.stream(DiaSemana.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/periodo_atividade_quadro/tipos")
    public List<String> listarTiposPeriodo() {
        return Arrays.stream(TipoPeridoAtividadeQuadro.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/usuarios/tipos")
    public List<String> listarTiposUsuario() {
        return Arrays.stream(TipoUsuario.values())
                .map(Enum::name)
                .toList();
    }
}