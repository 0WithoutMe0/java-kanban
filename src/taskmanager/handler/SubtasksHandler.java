package taskmanager.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler<Subtask> implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager, Subtask.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] request = uri.split("/");
        String method = exchange.getRequestMethod();

        if (!request[1].equals("subtasks")) {
            BaseHttpHandler.sendBadRequest(exchange);
            return;
        }

        super.handle(exchange);
    }

    @Override
    protected List<Subtask> getAll() {
        return taskManager.getAllSubtasks();
    }

    @Override
    protected Subtask getById(int id) {
        return taskManager.getSubtaskById(id);
    }

    @Override
    protected void add(Subtask task) {
        taskManager.addSubtask(task);
    }

    @Override
    protected void update(Subtask task) {
        taskManager.updateSubtask(task);
    }

    @Override
    protected void delete(int id) {
        taskManager.removeSubtaskById(id);
    }

    @Override
    protected boolean hasOverlaps(Subtask task) {
        return taskManager.isCrossedWithAllTasks(task);
    }
}
