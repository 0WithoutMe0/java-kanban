package taskmanager.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.adapter.DurationAdapter;
import taskmanager.adapter.LocalDateTimeAdapter;
import taskmanager.exeptions.NotFoundException;
import taskmanager.exeptions.TimeException;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseHttpHandler<T extends Task> implements HttpHandler {
    protected final TaskManager taskManager;

    protected final Gson gson;

    protected final Class<T> taskType;

    protected BaseHttpHandler(TaskManager taskManager, Class<T> taskType) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        this.taskType = taskType;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] request = uri.split("/");
        String method = exchange.getRequestMethod();


        if (request.length == 2 && method.equals("GET")) {
            handleGetAll(exchange);
        } else if (request.length == 3 && method.equals("GET")) {
            int id = Integer.parseInt(request[2]);
            handleGetById(exchange, id);
        } else if (request.length == 2 && method.equals("POST")) {
            handlePostCreate(exchange);
        } else if (request.length == 3 && method.equals("POST")) {
            int id = Integer.parseInt(request[2]);
            handlePostUpdate(exchange, id);
        } else if (request.length == 3 && method.equals("DELETE")) {
            int id = Integer.parseInt(request[2]);
            handleDelete(exchange, id);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        try {
            List<T> items = getAll();
            String response = gson.toJson(items);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (NotFoundException ex) {
            BaseHttpHandler.sendNotFound(exchange);
        }
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        try {
            T task = getById(id);
            String response = gson.toJson(task);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (NotFoundException ex) {
            BaseHttpHandler.sendNotFound(exchange);
        }
    }

    private void handlePostCreate(HttpExchange exchange) throws IOException {

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            T task = gson.fromJson(requestBody, taskType);

            add(task);
            String response = "Created task";
            exchange.sendResponseHeaders(201, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TimeException ex) {
            BaseHttpHandler.sendHasOverlaps(exchange);
        }
    }

    private void handlePostUpdate(HttpExchange exchange, int id) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            T task = gson.fromJson(requestBody, taskType);

            update(task);

            String response = "Updated task";
            exchange.sendResponseHeaders(201, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TimeException ex) {
            BaseHttpHandler.sendHasOverlaps(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        try {
            delete(id);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String response = "Deleted task";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (NotFoundException ex) {
            BaseHttpHandler.sendNotFound(exchange);
        }
    }

    protected static void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write("NOT FOUND".getBytes());
        h.close();
    }

    protected static void sendBadRequest(HttpExchange h) throws IOException {
        h.sendResponseHeaders(400, 0);
        h.getResponseBody().write("BAD REQUEST".getBytes());
        h.close();
    }

    protected static void sendHasOverlaps(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        h.getResponseBody().write("Задача пересекается с существующими".getBytes());
        h.close();
    }

    protected abstract List<T> getAll();
    protected abstract T getById(int id);
    protected abstract void add(T task);
    protected abstract void update(T task);
    protected abstract void delete(int id);
    protected abstract boolean hasOverlaps(T task);
}
