package com.fatec.gini.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.domain.services.UsuarioService;
import com.fatec.gini.dto.login.LoginRequest;
import com.fatec.gini.dto.login.LoginResponse;
import com.fatec.gini.dto.usuario.UsuarioRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Profile("prod")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody @Valid UsuarioRequest data) {
        if (this.usuarioService.jaUsuarioExisteEmail(data.email())) {
            return ResponseEntity.badRequest().build();
        }

        this.usuarioService.criar(data);
        return ResponseEntity.ok().build();
    }

}
