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

public class HttpTaskServerHistoryTest extends DataForTest{
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
        manager.getTaskById(0);
        manager.getSubtaskById(4);
        manager.getEpicById(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(List.of(task1, subtask1, epic1)), response.body());
    }

    @AfterEach
    public void shutDown() throws IOException {
        server.stop();
    }

}
