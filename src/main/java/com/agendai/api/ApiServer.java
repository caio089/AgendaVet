package com.agendai.api;

import com.agendai.database.DataSeeder;
import com.agendai.database.DatabaseConnection;
import com.agendai.database.DatabaseInitializer;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP simples (JDK HttpServer) — sem Spring Boot.
 *
 * - /api/*  → REST JSON (CRUD SQLite)
 * - /*      → frontend estático (HTML + Tailwind + JS)
 */
public final class ApiServer {

    public static final int PORT = 8080;

    private ApiServer() {
    }

    public static void start() throws Exception {
        DatabaseInitializer.init();
        DataSeeder.seedIfEmpty();

        Path frontend = Path.of("frontend");
        if (!frontend.toFile().isDirectory()) {
            throw new IllegalStateException(
                    "Pasta 'frontend' não encontrada. Execute o servidor na raiz do projeto AgendaVet."
            );
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api", new ApiDispatcher());
        server.createContext("/", new StaticFileHandler(frontend));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(1);
            DatabaseConnection.fecharConexao();
            System.out.println("\n[Servidor] Encerrado.");
        }));

        System.out.println("========================================");
        System.out.println("  AgendaVet — Servidor HTTP iniciado");
        System.out.println("  Frontend: http://localhost:" + PORT);
        System.out.println("  API REST: http://localhost:" + PORT + "/api/tutores");
        System.out.println("  Banco:    agendavet.db (SQLite)");
        System.out.println("========================================");
    }
}
