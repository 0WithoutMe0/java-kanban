import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.adapter.DurationAdapter;
import taskmanager.adapter.LocalDateTimeAdapter;
import taskmanager.managers.Managers;
import taskmanager.managers.TaskManager;
import taskmanager.server.HttpTaskServer;
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

public class HttpTaskServerEpicTest extends DataForTest {

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
    public void testGetAllEpic() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(List.of(epic1, epic2), tasksFromManager, "Задачи не совпадают");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic taskFromManager = manager.getEpicById(3);

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(epic2, taskFromManager, "Задачи не совпадают");

        //нет задачи
        URI url_ = URI.create("http://localhost:8080/epics/11");
        HttpRequest request_ = HttpRequest.newBuilder().uri(url_).GET().build();


        HttpResponse<String> response_ = client.send(request_, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response_.statusCode());
    }

    @Test
    public void testPostCreateEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, new ArrayList<>());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testPostUpdateEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Test 2", "Testing task 2",
                Status.NEW, TaskType.MIDDLE_TASK, new ArrayList<>());
        String taskJson1 = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics/");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();


        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        task.setName("Updated task");
        task.setId(0);
        String taskJson2 = gson.toJson(task);
        URI url2 = URI.create("http://localhost:8080/epics/0");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();


        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Updated task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @AfterEach
    public void shutDown() throws IOException {
        server.stop();
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(List.of(epic1), manager.getAllEpics(), "Задачи не совпадают");
    }

    @Test
    public void testGetEpicSubtasksById() throws IOException, InterruptedException {
        initializeCommonTestData(manager);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(List.of(subtask2), manager.getEpicById(3).getSubtasks(), "Задачи не совпадают");
    }

}
