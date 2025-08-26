package taskmanager.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.Task;


import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler<Task> implements HttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager, Task.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] request = uri.split("/");
        String method = exchange.getRequestMethod();

        if (!request[1].equals("tasks")) {
            BaseHttpHandler.sendBadRequest(exchange);
            return;
        }

        super.handle(exchange);
    }

    @Override
    protected List<Task> getAll() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Task getById(int id) {
        return taskManager.getTaskById(id);
    }

    @Override
    protected void add(Task task) {
        taskManager.addTask(task);
    }

    @Override
    protected void update(Task task) {
        taskManager.updateTask(task);
    }

    @Override
    protected void delete(int id) {
        taskManager.removeTaskById(id);
    }

    @Override
    protected boolean hasOverlaps(Task task) {
        return taskManager.isCrossedWithAllTasks(task);
    }
}
