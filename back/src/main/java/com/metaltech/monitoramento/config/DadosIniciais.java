package com.metaltech.monitoramento.config;

import com.metaltech.monitoramento.domain.enums.Perfil;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.domain.model.Usuario;
import com.metaltech.monitoramento.repository.EsteiraRepository;
import com.metaltech.monitoramento.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria os dados mínimos para desenvolvimento (somente no perfil dev):
 * - 1 esteira de triagem
 * - usuário ADMIN:    admin@metaltech.com   / admin123
 * - usuário OPERADOR: esteira@metaltech.com / esteira123 (usado pela automação)
 * Só cria o que ainda não existe, então pode reiniciar a API à vontade.
 */
@Component
@Profile("dev")
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final EsteiraRepository esteiraRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(EsteiraRepository esteiraRepository,
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.esteiraRepository = esteiraRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (esteiraRepository.count() == 0) {
            esteiraRepository.save(new Esteira(
                    "Esteira de Triagem 01", "Linha 1 - MetalTech", 30.0, 70.0, 90.0));
            log.info("Esteira inicial criada.");
        }

        criarUsuarioSeNaoExistir("Administrador", "admin@metaltech.com", "admin123", Perfil.ADMIN);
        criarUsuarioSeNaoExistir("Esteira de Triagem", "esteira@metaltech.com", "esteira123", Perfil.OPERADOR);
    }

    private void criarUsuarioSeNaoExistir(String nome, String email, String senha, Perfil perfil) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(new Usuario(nome, email, passwordEncoder.encode(senha), perfil));
            log.info("Usuário inicial criado: {} ({})", email, perfil);
        }
    }
}