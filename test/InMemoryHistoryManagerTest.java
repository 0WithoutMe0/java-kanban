import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {

    Managers managers;
    InMemoryTaskManager memoryTaskManager;
    Task task1;
    Task task2;
    Subtask subtask1;
    Subtask subtask2;
    Epic epic1;
    Epic epic2;


    @BeforeEach
    void beforeEach() {
        managers = new Managers();
        memoryTaskManager = (InMemoryTaskManager) managers.getDefault();
        task1 = new Task("Задача 1", "ДЗ 1", Status.NEW, TaskType.MIDDLE_TASK);
        memoryTaskManager.addTask(task1);
        task2 = new Task("Задача 2", "ДЗ 2", Status.NEW, TaskType.MIDDLE_TASK);
        memoryTaskManager.addTask(task2);
        epic1 = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        memoryTaskManager.addEpic(epic1);
        epic2 = new Epic("Эпик 2", "ДЗ 2", Status.NEW, TaskType.EPIC, new ArrayList<>());
        memoryTaskManager.addEpic(epic2);
        subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic1.getId());
        memoryTaskManager.addSubtask(subtask1);
        subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic2.getId());
        memoryTaskManager.addSubtask(subtask2);
    }


    @Test
    void emptyList() {
        assertEquals(0, memoryTaskManager.getHistory().size());
    }


    @Test
    void historyShouldNotContainDuplicates() {
        memoryTaskManager.getTaskById(task1.getId());
        memoryTaskManager.getTaskById(task1.getId());

        assertEquals(1, memoryTaskManager.getHistory().size());
    }


    @Test
    void historyManagerShouldKeepTaskVersions() {
        memoryTaskManager.getTaskById(task1.getId());

        Task updatedTask = new Task("Задача 1.1", "ДЗ 1.1", Status.IN_PROGRESS, TaskType.MIDDLE_TASK);
        memoryTaskManager.updateTask(updatedTask);

        memoryTaskManager.getTaskById(updatedTask.getId());

        List<Task> history = memoryTaskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals("Задача 1.1", history.get(0).getName());
    }


    @Test
    void removeTaskShouldCorrectlyUpdateHistory() {
        memoryTaskManager.getTaskById(task1.getId());
        assertEquals(1, memoryTaskManager.getHistory().size(), "История должна содержать 1 элемент");

        memoryTaskManager.removeTaskById(task1.getId());
        assertEquals(0, memoryTaskManager.getHistory().size(), "История должна быть пустой после удаления");
        assertTrue(memoryTaskManager.getHistory().isEmpty(), "История должна быть пустой");


        memoryTaskManager.getSubtaskById(subtask1.getId());
        memoryTaskManager.getTaskById(task2.getId());
        memoryTaskManager.getEpicById(epic1.getId());

        assertEquals(List.of(subtask1, task2, epic1), memoryTaskManager.getHistory(), "Неверный состав истории");

        memoryTaskManager.removeTaskById(task2.getId());
        assertEquals(List.of(subtask1, epic1), memoryTaskManager.getHistory(), "Неверная история после удаления");


        memoryTaskManager.getSubtaskById(subtask1.getId());
        assertEquals(List.of(epic1, subtask1), memoryTaskManager.getHistory());
    }
}
