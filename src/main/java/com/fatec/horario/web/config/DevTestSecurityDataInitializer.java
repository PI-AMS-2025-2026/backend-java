package com.fatec.horario.web.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;
import com.fatec.horario.security.SecurityRoles;

@Configuration
public class DevTestSecurityDataInitializer {

    @Bean
    @Profile({ "dev", "test" })
    CommandLineRunner initAuthUsers(
            TipoUsuarioRepository tipoUsuarioRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            TipoUsuario admin = findOrCreateTipoUsuario(tipoUsuarioRepository, SecurityRoles.ADMIN);
            TipoUsuario coordenador = findOrCreateTipoUsuario(tipoUsuarioRepository, SecurityRoles.COORDENADOR);
            TipoUsuario professor = findOrCreateTipoUsuario(tipoUsuarioRepository, SecurityRoles.PROFESSOR);

            upsertUser(usuarioRepository, passwordEncoder, admin,
                    "Administrador", "admin@fatec.local", "123456", "Sao Paulo");
            upsertUser(usuarioRepository, passwordEncoder, coordenador,
                    "Coordenador", "coordenador@fatec.local", "123456", "Sao Paulo");
            upsertUser(usuarioRepository, passwordEncoder, professor,
                    "Professor", "professor@fatec.local", "123456", "Sao Paulo");
        };
    }

    private TipoUsuario findOrCreateTipoUsuario(TipoUsuarioRepository repository, String roleName) {
        return repository.findByNomeIgnoreCase(roleName)
                .orElseGet(() -> repository.save(new TipoUsuario(null, roleName, List.of())));
    }

    private void upsertUser(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            TipoUsuario tipoUsuario,
            String nome,
            String email,
            String senha,
            String cidade) {

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseGet(Usuario::new);

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setCidade(cidade);
        usuario.setStatus(Status.ATIVO);
        usuario.setTipo_usuario(tipoUsuario);

        if (usuario.getCreated_at() == null) {
            usuario.setCreated_at(LocalDateTime.now());
        }
        usuario.setUpdated_at(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }
}
