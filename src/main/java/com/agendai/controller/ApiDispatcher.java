package com.agendai.controller;

import com.agendai.dao.AnimalDAO;
import com.agendai.dao.AnimalDAOImpl;
import com.agendai.dao.ConsultaDAO;
import com.agendai.dao.ConsultaDAOImpl;
import com.agendai.dao.TutorDAO;
import com.agendai.dao.TutorDAOImpl;
import com.agendai.dao.UsuarioDAO;
import com.agendai.dao.UsuarioDAOImpl;
import com.agendai.dao.VeterinarioDAO;
import com.agendai.dao.VeterinarioDAOImpl;
import com.agendai.model.Animal;
import com.agendai.model.Consulta;
import com.agendai.model.Tutor;
import com.agendai.model.Usuario;
import com.agendai.model.Veterinario;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Roteador REST central — expõe CRUD de todas as entidades via /api/*
 */
public class ApiDispatcher implements HttpHandler {

    private final TutorDAO tutorDAO = new TutorDAOImpl();
    private final AnimalDAO animalDAO = new AnimalDAOImpl();
    private final VeterinarioDAO VeterinarioDAO = new VeterinarioDAOImpl();
    private final ConsultaDAO consultaDAO = new ConsultaDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.handleOptions(exchange);
            return;
        }

        String path = normalizePath(exchange);
        String method = exchange.getRequestMethod().toUpperCase();

        try {
            if (path.startsWith("/api/auth")) {
                handleAuth(exchange, method, path);
                return;
            }

            String token = HttpUtil.extractBearerToken(exchange);
            if (SessionManager.getUsuario(token) == null) {
                HttpUtil.sendError(exchange, 401, "Não autenticado. Faça login.");
                return;
            }

            if (path.startsWith("/api/tutores")) {
                handleTutores(exchange, method, path);
            } else if (path.startsWith("/api/animais")) {
                handleAnimais(exchange, method, path);
            } else if (path.startsWith("/api/veterinarios")) {
                handleVeterinarios(exchange, method, path);
            } else if (path.startsWith("/api/consultas")) {
                handleConsultas(exchange, method, path);
            } else if ("/api/dashboard".equals(path) && "GET".equals(method)) {
                handleDashboard(exchange);
            } else {
                HttpUtil.sendError(exchange, 404, "Rota não encontrada: " + path);
            }
        } catch (RuntimeException e) {
            HttpUtil.sendError(exchange, 400, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Autenticação
    // -------------------------------------------------------------------------

    private void handleAuth(HttpExchange exchange, String method, String path) throws IOException {
        if ("/api/auth/login".equals(path) && "POST".equals(method)) {
            LoginRequest login = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), LoginRequest.class);
            if (login.getEmail() == null || login.getSenha() == null) {
                HttpUtil.sendError(exchange, 400, "Informe e-mail e senha.");
                return;
            }

            Usuario usuario = usuarioDAO.autenticar(login.getEmail(), login.getSenha());
            if (usuario == null) {
                HttpUtil.sendError(exchange, 401, "E-mail ou senha inválidos.");
                return;
            }

            String token = SessionManager.createSession(usuario);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("token", token);
            body.put("usuario", toPublicUser(usuario));
            HttpUtil.sendJson(exchange, 200, body);
            return;
        }

        if ("/api/auth/logout".equals(path) && "POST".equals(method)) {
            SessionManager.invalidate(HttpUtil.extractBearerToken(exchange));
            HttpUtil.sendNoContent(exchange);
            return;
        }

        if ("/api/auth/me".equals(path) && "GET".equals(method)) {
            Usuario usuario = SessionManager.getUsuario(HttpUtil.extractBearerToken(exchange));
            if (usuario == null) {
                HttpUtil.sendError(exchange, 401, "Sessão inválida ou expirada.");
                return;
            }
            HttpUtil.sendJson(exchange, 200, toPublicUser(usuario));
            return;
        }

        HttpUtil.sendError(exchange, 404, "Rota de autenticação não encontrada.");
    }

    private Map<String, Object> toPublicUser(Usuario usuario) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", usuario.getId());
        user.put("nome", usuario.getNome());
        user.put("email", usuario.getEmail());
        user.put("perfil", usuario.getPerfil());
        return user;
    }

    private void handleDashboard(HttpExchange exchange) throws IOException {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("tutores", tutorDAO.listar().size());
        stats.put("animais", animalDAO.listar().size());
        stats.put("veterinarios", VeterinarioDAO.listar().size());
        stats.put("consultas", consultaDAO.listar().size());
        HttpUtil.sendJson(exchange, 200, stats);
    }

    // -------------------------------------------------------------------------
    // Tutores
    // -------------------------------------------------------------------------

    private void handleTutores(HttpExchange exchange, String method, String path) throws IOException {
        Integer id = HttpUtil.extractId(path, "/api/tutores");
        if (id != null && id < 0) {
            HttpUtil.sendError(exchange, 404, "ID inválido");
            return;
        }

        switch (method) {
            case "GET" -> {
                if (id == null) {
                    HttpUtil.sendJson(exchange, 200, tutorDAO.listar());
                } else {
                    Tutor tutor = tutorDAO.buscarPorId(id);
                    if (tutor == null) {
                        HttpUtil.sendError(exchange, 404, "Tutor não encontrado");
                    } else {
                        HttpUtil.sendJson(exchange, 200, tutor);
                    }
                }
            }
            case "POST" -> {
                Tutor tutor = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Tutor.class);
                tutorDAO.salvar(tutor);
                HttpUtil.sendJson(exchange, 201, tutor);
            }
            case "PUT" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                Tutor tutor = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Tutor.class);
                tutor.setId(id);
                if (tutorDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Tutor não encontrado");
                    return;
                }
                tutorDAO.atualizar(tutor);
                HttpUtil.sendJson(exchange, 200, tutorDAO.buscarPorId(id));
            }
            case "DELETE" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                if (tutorDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Tutor não encontrado");
                    return;
                }
                tutorDAO.deletar(id);
                HttpUtil.sendNoContent(exchange);
            }
            default -> HttpUtil.sendError(exchange, 405, "Método não permitido");
        }
    }

    // -------------------------------------------------------------------------
    // Animais
    // -------------------------------------------------------------------------

    private void handleAnimais(HttpExchange exchange, String method, String path) throws IOException {
        Integer id = HttpUtil.extractId(path, "/api/animais");
        if (id != null && id < 0) {
            HttpUtil.sendError(exchange, 404, "ID inválido");
            return;
        }

        switch (method) {
            case "GET" -> {
                if (id == null) {
                    HttpUtil.sendJson(exchange, 200, animalDAO.listar());
                } else {
                    Animal animal = animalDAO.buscarPorId(id);
                    if (animal == null) {
                        HttpUtil.sendError(exchange, 404, "Animal não encontrado");
                    } else {
                        HttpUtil.sendJson(exchange, 200, animal);
                    }
                }
            }
            case "POST" -> {
                Animal animal = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Animal.class);
                animalDAO.salvar(animal);
                HttpUtil.sendJson(exchange, 201, animal);
            }
            case "PUT" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                Animal animal = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Animal.class);
                animal.setId(id);
                if (animalDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Animal não encontrado");
                    return;
                }
                animalDAO.atualizar(animal);
                HttpUtil.sendJson(exchange, 200, animalDAO.buscarPorId(id));
            }
            case "DELETE" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                if (animalDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Animal não encontrado");
                    return;
                }
                animalDAO.deletar(id);
                HttpUtil.sendNoContent(exchange);
            }
            default -> HttpUtil.sendError(exchange, 405, "Método não permitido");
        }
    }

    // -------------------------------------------------------------------------
    // Veterinários
    // -------------------------------------------------------------------------

    private void handleVeterinarios(HttpExchange exchange, String method, String path) throws IOException {
        if (!"GET".equals(method)) {
            String token = HttpUtil.extractBearerToken(exchange);
            Usuario u = SessionManager.getUsuario(token);
            if (u != null && "recepcao".equals(u.getPerfil())) {
                HttpUtil.sendError(exchange, 403, "Acesso negado. Apenas administradores podem gerenciar veterinários.");
                return;
            }
        }

        Integer id = HttpUtil.extractId(path, "/api/veterinarios");
        if (id != null && id < 0) {
            HttpUtil.sendError(exchange, 404, "ID inválido");
            return;
        }

        switch (method) {
            case "GET" -> {
                if (id == null) {
                    HttpUtil.sendJson(exchange, 200, VeterinarioDAO.listar());
                } else {
                    Veterinario vet = VeterinarioDAO.buscarPorId(id);
                    if (vet == null) {
                        HttpUtil.sendError(exchange, 404, "Veterinário não encontrado");
                    } else {
                        HttpUtil.sendJson(exchange, 200, vet);
                    }
                }
            }
            case "POST" -> {
                Veterinario vet = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Veterinario.class);
                VeterinarioDAO.salvar(vet);
                HttpUtil.sendJson(exchange, 201, vet);
            }
            case "PUT" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                Veterinario vet = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Veterinario.class);
                vet.setId(id);
                if (VeterinarioDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Veterinário não encontrado");
                    return;
                }
                VeterinarioDAO.atualizar(vet);
                HttpUtil.sendJson(exchange, 200, VeterinarioDAO.buscarPorId(id));
            }
            case "DELETE" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                if (VeterinarioDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Veterinário não encontrado");
                    return;
                }
                VeterinarioDAO.deletar(id);
                HttpUtil.sendNoContent(exchange);
            }
            default -> HttpUtil.sendError(exchange, 405, "Método não permitido");
        }
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    private void handleConsultas(HttpExchange exchange, String method, String path) throws IOException {
        Integer id = HttpUtil.extractId(path, "/api/consultas");
        if (id != null && id < 0) {
            HttpUtil.sendError(exchange, 404, "ID inválido");
            return;
        }

        switch (method) {
            case "GET" -> {
                if (id == null) {
                    List<Consulta> lista = consultaDAO.listar();
                    HttpUtil.sendJson(exchange, 200, lista);
                } else {
                    Consulta consulta = consultaDAO.buscarPorId(id);
                    if (consulta == null) {
                        HttpUtil.sendError(exchange, 404, "Consulta não encontrada");
                    } else {
                        HttpUtil.sendJson(exchange, 200, consulta);
                    }
                }
            }
            case "POST" -> {
                Consulta consulta = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Consulta.class);
                consultaDAO.salvar(consulta);
                HttpUtil.sendJson(exchange, 201, consulta);
            }
            case "PUT" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                Consulta consulta = HttpUtil.gson().fromJson(HttpUtil.readBody(exchange), Consulta.class);
                consulta.setId(id);
                if (consultaDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Consulta não encontrada");
                    return;
                }
                consultaDAO.atualizar(consulta);
                HttpUtil.sendJson(exchange, 200, consultaDAO.buscarPorId(id));
            }
            case "DELETE" -> {
                if (id == null) {
                    HttpUtil.sendError(exchange, 400, "Informe o ID na URL");
                    return;
                }
                if (consultaDAO.buscarPorId(id) == null) {
                    HttpUtil.sendError(exchange, 404, "Consulta não encontrada");
                    return;
                }
                consultaDAO.deletar(id);
                HttpUtil.sendNoContent(exchange);
            }
            default -> HttpUtil.sendError(exchange, 405, "Método não permitido");
        }
    }

    /** Garante path completo /api/... independente do contexto HttpServer */
    private String normalizePath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api")) {
            return path;
        }
        return "/api" + (path.startsWith("/") ? path : "/" + path);
    }
}
