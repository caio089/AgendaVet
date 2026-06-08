package com.agendai.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utilitários HTTP compartilhados pelos handlers da API.
 */
public final class HttpUtil {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private HttpUtil() {
    }

    public static Gson gson() {
        return GSON;
    }

    /** Headers CORS — permite o frontend consumir a API na mesma origem ou em dev */
    public static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public static void handleOptions(HttpExchange exchange) throws IOException {
        addCors(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    public static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        addCors(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, Map.of("message", message));
    }

    public static void sendNoContent(HttpExchange exchange) throws IOException {
        addCors(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    /** Extrai token Bearer do header Authorization */
    public static String extractBearerToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        return auth.substring(7).trim();
    }

    /** Extrai ID numérico de paths como /api/tutores/5 */
    public static Integer extractId(String path, String basePath) {
        if (path.equals(basePath)) {
            return null;
        }
        if (!path.startsWith(basePath + "/")) {
            return -1;
        }
        try {
            return Integer.parseInt(path.substring(basePath.length() + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
