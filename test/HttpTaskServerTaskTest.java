import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.adapter.DurationAdapter;
import taskmanager.adapter.LocalDateTimeAdapter;
import taskmanager.managers.Managers;
import taskmanager.managers.TaskManager;
import taskmanager.service.HttpTaskServer;
import taskmanager.tasks.Status;
import taskmanager.tasks.Task;
import taskmanager.tasks.TaskType;



import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTaskTest extends DataForTest {

    TaskManager manager;

    HttpTaskServer server;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
        server.start();
    }

    @Test
    public void testGetAllTask() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(List.of(task1, task2), tasksFromManager, "Задачи не совпадают");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromManager = manager.getTaskById(1);

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(task2, taskFromManager, "Задачи не совпадают");

        //нет задачи
        URI url_ = URI.create("http://localhost:8080/tasks/11");
        HttpRequest request_ = HttpRequest.newBuilder().uri(url_).GET().build();


        HttpResponse<String> response_ = client.send(request_, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response_.statusCode());
    }

    @Test
    public void testPostCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //пересечение
        Task task3 = new Task("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson3 = gson.toJson(task3);

        URI url3 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();


        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response3.statusCode());
    }

    @Test
    public void testPostUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();


        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        task.setName("Updated task");
        task.setId(0);
        String taskJson2 = gson.toJson(task);
        URI url2 = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();


        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Updated task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //пересечение
        Task task3 = new Task("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson3 = gson.toJson(task3);

        URI url3 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();


        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response3.statusCode());
    }

    @AfterEach
    public void shutDown() throws IOException {
        server.stop();
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(List.of(task1), manager.getAllTasks(), "Задачи не совпадают");
    }

}
