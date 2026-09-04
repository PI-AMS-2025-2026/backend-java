package com.fatec.gini.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.RefreshTokenService;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.dto.auth.LoginRequest;
import com.fatec.gini.dto.auth.LoginResponse;
import com.fatec.gini.dto.auth.RefreshTokenRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Autenticacao", description = "Endpoints para login, renovacao e encerramento de sessoes")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

        private final AuthenticationManager authenticationManager;

        private final TokenService tokenService;
        private final RefreshTokenService refreshTokenService;

        @Operation(summary = "Realizar login", description = "Autentica o usuario e retorna tokens de acesso e de renovacao.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Dados de login invalidos", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Email ou senha invalidos", content = @Content)
        })
        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

                // Converte as credenciais recebidas em um objeto reconhecido pelo Spring Security.
                var usernamePassword = new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.senha());

                // Verifica as credenciais e recupera o usuario autenticado.
                var authentication = authenticationManager.authenticate(usernamePassword);

                Usuario usuario = (Usuario) authentication.getPrincipal();

                // Emite um token curto para as requisicoes e um refresh token para renova-lo.
                String accessToken = tokenService.gerarToken(usuario);

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());

                return ResponseEntity.ok(
                                new LoginResponse(
                                                accessToken,
                                                refreshToken.getToken()));
        }

        @Operation(summary = "Renovar tokens", description = "Valida o refresh token e gera um novo par de tokens.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Refresh token ausente ou invalido", content = @Content)
        })
        @PostMapping("/refresh")
        public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
                return ResponseEntity.ok(refreshTokenService.refresh(request.refreshToken()));
        }

        @Operation(summary = "Encerrar sessao", description = "Revoga o refresh token informado.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Sessao encerrada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Refresh token invalido", content = @Content)
        })
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest request) {
                // A revogacao impede que o refresh token seja reutilizado.
                refreshTokenService.revoke(request.refreshToken());
                return ResponseEntity.noContent().build();
        }
}
