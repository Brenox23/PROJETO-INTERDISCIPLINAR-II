package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.domain.model.Usuario;
import com.metaltech.monitoramento.dto.request.UsuarioRequest;
import com.metaltech.monitoramento.dto.response.UsuarioResponse;
import com.metaltech.monitoramento.exception.RecursoNaoEncontradoException;
import com.metaltech.monitoramento.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Cadastro e gestão de usuários. A senha é sempre convertida em hash
 * (BCrypt) antes de chegar à entidade.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Já existe um usuário cadastrado com este e-mail.");
        }
        Usuario usuario = new Usuario(
                request.nome(),
                email,
                passwordEncoder.encode(request.senha()),
                request.perfil()
        );
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("nome")).stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        String email = normalizarEmail(request.email());

        boolean emailEmUsoPorOutro = usuarioRepository.findByEmail(email)
                .filter(outro -> !outro.getId().equals(id))
                .isPresent();
        if (emailEmUsoPorOutro) {
            throw new IllegalStateException("Este e-mail já está em uso por outro usuário.");
        }

        usuario.atualizarDados(request.nome(), email);
        usuario.alterarSenha(passwordEncoder.encode(request.senha()));
        usuario.alterarPerfil(request.perfil());
        return UsuarioResponse.de(usuario);
    }

    /** Exclusão lógica: o usuário perde o acesso, mas o registro é mantido. */
    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).desativar();
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}