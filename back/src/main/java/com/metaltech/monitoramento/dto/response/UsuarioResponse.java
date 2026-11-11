package com.metaltech.monitoramento.dto.response;

import com.metaltech.monitoramento.domain.enums.Perfil;
import com.metaltech.monitoramento.domain.model.Usuario;

import java.time.LocalDateTime;

/**
 * Resposta de usuário SEM a senha (nem o hash), por segurança.
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo,
        LocalDateTime criadoEm
) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isAtivo(),
                usuario.getCriadoEm()
        );
    }
}