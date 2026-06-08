package com.agendai.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serve arquivos estáticos do frontend (HTML, CSS, JS).
 */
public class StaticFileHandler implements HttpHandler {

    private final Path frontendRoot;

    public StaticFileHandler(Path frontendRoot) {
        this.frontendRoot = frontendRoot.toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendError(exchange, 405, "Método não permitido");
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if ("/".equals(requestPath) || requestPath.isEmpty()) {
            requestPath = "/index.html";
        }

        Path filePath = frontendRoot.resolve(requestPath.substring(1)).normalize();

        // Proteção contra path traversal
        if (!filePath.startsWith(frontendRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            HttpUtil.sendError(exchange, 404, "Arquivo não encontrado");
            return;
        }

        byte[] bytes = Files.readAllBytes(filePath);
        String contentType = detectContentType(filePath.toString());

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String detectContentType(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
