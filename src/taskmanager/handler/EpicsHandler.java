package taskmanager.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler<Epic> implements HttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager, Epic.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] request = uri.split("/");
        String method = exchange.getRequestMethod();

        if (!request[1].equals("epics")) {
            BaseHttpHandler.sendBadRequest(exchange);
            return;
        }
        if (request.length == 4 && method.equals("GET")) {
            int id = Integer.parseInt(request[2]);
            handleGetSubtasks(exchange, id);
        } else {
            super.handle(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange, int id) throws IOException {
        Epic epic = getById(id);
        if (epic == null) {
            BaseHttpHandler.sendNotFound(exchange);
        }
        List<Subtask> subtasks = epic.getSubtasks();
        String response = gson.toJson(subtasks);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    @Override
    protected List<Epic> getAll() {
        return taskManager.getAllEpics();
    }

    @Override
    protected Epic getById(int id) {
        return taskManager.getEpicById(id);
    }

    @Override
    protected void add(Epic task) {
        taskManager.addEpic(task);
    }

    @Override
    protected void update(Epic task) {
        taskManager.updateEpic(task);
    }

    @Override
    protected void delete(int id) {
        taskManager.removeEpicById(id);
    }

    @Override
    protected boolean hasOverlaps(Epic task) {
        return taskManager.isCrossedWithAllTasks(task);
    }
}
