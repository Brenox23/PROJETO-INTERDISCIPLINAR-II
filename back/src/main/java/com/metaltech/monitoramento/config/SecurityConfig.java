package com.metaltech.monitoramento.config;

import com.metaltech.monitoramento.domain.model.Usuario;
import com.metaltech.monitoramento.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Controle de acesso da API.
 *
 * Autenticação: HTTP Basic (e-mail + senha), sem sessão (stateless).
 * Autorização por perfil:
 * - Swagger e consultas (GET): liberados -> dashboard e RA
 * - Envio de leituras (POST /api/leituras): ADMIN ou OPERADOR -> automação
 * - Resolver alertas (PATCH /api/alertas): ADMIN ou OPERADOR
 * - Exportação CSV: qualquer usuário autenticado -> Ciência de Dados
 * - Cadastro de esteiras e usuários: somente ADMIN
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Documentação e erros
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Escrita controlada por perfil
                        .requestMatchers(HttpMethod.POST, "/api/leituras/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/alertas/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/exportacoes/**").authenticated()

                        // Consultas liberadas para dashboard e RA
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // Demais operações de esteiras (POST, PUT, PATCH, DELETE)
                        .requestMatchers("/api/esteiras/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /** Senhas armazenadas com hash BCrypt (nunca em texto puro). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Busca o usuário no banco pelo e-mail. Usuários desativados não conseguem entrar.
     */
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> usuarioRepository.findByEmail(email.trim().toLowerCase())
                .filter(Usuario::isAtivo)
                .map(usuario -> User.withUsername(usuario.getEmail())
                        .password(usuario.getSenhaHash())
                        .authorities(usuario.getPerfil().getAuthority())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado ou inativo."));
    }
}