package com.metaltech.monitoramento.domain.enums;

/**
 * Perfis de acesso dos usuários da plataforma.
 */
public enum Perfil {

    ADMIN("Administrador"),
    OPERADOR("Operador"),
    VISUALIZADOR("Visualizador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Nome da permissão no formato esperado pelo Spring Security.
     */
    public String getAuthority() {
        return "ROLE_" + name();
    }
}