package com.fatec.horario.security;

import com.fatec.horario.infrastructure.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DetalhesUsuarioServiceImpl implements UserDetailsService {

    private final UsuarioRepository repository;

    public DetalhesUsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}