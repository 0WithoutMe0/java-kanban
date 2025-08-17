import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EpicStatusTest {

    InMemoryTaskManager memoryTaskManager;

    @BeforeEach
    void setUp() {
         memoryTaskManager = (InMemoryTaskManager)Managers.getDefault();
    }

    @Test
    void epicStatusNewTest() {
        //Все по NEW
        Epic epic = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        memoryTaskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(10));
        memoryTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
        memoryTaskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Субзадача 3", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        memoryTaskManager.addSubtask(subtask3);

        assertEquals(Status.NEW, epic.getStatus());

        //C IN_PROGRESS
        subtask1.setStatus(Status.IN_PROGRESS);
        memoryTaskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        //NEW и DONE
        subtask1.setStatus(Status.DONE);
        memoryTaskManager.updateSubtask(subtask1);
        assertEquals(Status.NEW, epic.getStatus());

        //Все по DONE
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        memoryTaskManager.updateSubtask(subtask2);
        memoryTaskManager.updateSubtask(subtask3);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void isEpicExist() {
        Epic epic = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        memoryTaskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(10));
        memoryTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, -1, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));

        assertThrows(RuntimeException.class, () -> { memoryTaskManager.addSubtask(subtask2); });
    }

    @Test
    void isTaskCrossed() {
        Epic epic = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        memoryTaskManager.addEpic(epic);

        //Пересечение
        Subtask subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(10));
        assertTrue(memoryTaskManager.isCrossed(subtask1, subtask2));

        //Не пересечение
        Subtask subtask3 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask subtask4 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic.getId(), LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
        assertFalse(memoryTaskManager.isCrossed(subtask3, subtask4));
    }

}
