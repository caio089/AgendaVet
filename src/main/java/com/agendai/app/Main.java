package com.agendai.app;

import com.agendai.api.ApiServer;

/**
 * Ponto de entrada da aplicação AgendaVet.
 * Inicializa banco SQLite e sobe o servidor HTTP (API REST + frontend).
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ApiServer.start();
    }
}
