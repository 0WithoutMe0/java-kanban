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
import taskmanager.tasks.*;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtaskTest extends DataForTest {

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
    public void testGetAllSubtask() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(List.of(subtask1, subtask2), tasksFromManager, "Задачи не совпадают");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromManager = manager.getSubtaskById(5);

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(subtask2, taskFromManager, "Задачи не совпадают");

        //нет задачи
        URI url_ = URI.create("http://localhost:8080/tasks/11");
        HttpRequest request_ = HttpRequest.newBuilder().uri(url_).GET().build();


        HttpResponse<String> response_ = client.send(request_, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response_.statusCode());
    }

    @Test
    public void testPostCreateSubtask() throws IOException, InterruptedException {
        epic1 = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        manager.addEpic(epic1);
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //пересечение
        Subtask task3 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson3 = gson.toJson(task3);

        URI url3 = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();


        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response3.statusCode());
    }

    @Test
    public void testPostUpdateSubtask() throws IOException, InterruptedException {
        epic1 = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        manager.addEpic(epic1);
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();


        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        subtask.setName("Updated task");
        subtask.setId(1);
        String taskJson2 = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();


        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Updated task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //пересечение
        Subtask subtask3 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson3 = gson.toJson(subtask3);

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
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(List.of(subtask1), manager.getAllSubtasks(), "Задачи не совпадают");
    }

}
