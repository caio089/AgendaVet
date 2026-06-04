package com.agendai.api;

import com.agendai.app.Usuario;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sessões em memória (token UUID → usuário logado).
 * Adequado para projeto acadêmico com servidor HTTP simples.
 */
public final class SessionManager {

    private static final Map<String, Usuario> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static String createSession(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        SESSIONS.put(token, usuario);
        return token;
    }

    public static Usuario getUsuario(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return SESSIONS.get(token);
    }

    public static void invalidate(String token) {
        if (token != null) {
            SESSIONS.remove(token);
        }
    }
}
