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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerPrioritizedTest extends DataForTest{
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
    public void testGetHistory() throws IOException, InterruptedException {
        initializeCommonTestData(manager);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        System.out.println(response.body());
        assertEquals(gson.toJson(List.of(task1, task2, subtask1, subtask2)), response.body());
    }

    @AfterEach
    public void shutDown() throws IOException {
        server.stop();
    }
}
