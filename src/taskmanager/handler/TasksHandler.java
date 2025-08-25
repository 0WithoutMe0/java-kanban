package taskmanager.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import taskmanager.adapter.DurationAdapter;
import taskmanager.adapter.LocalDateTimeAdapter;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.Status;
import taskmanager.tasks.Task;
import taskmanager.tasks.TaskType;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
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
