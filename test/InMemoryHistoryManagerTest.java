import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.tasks.*;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest extends DataForTest{

    InMemoryTaskManager memoryTaskManager;



    @BeforeEach
    void beforeEach() {
        memoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        initializeCommonTestData(memoryTaskManager);
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

        assertEquals(2, history.size());
        assertEquals("Задача 1.1", history.get(1).getName());
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

    @Test
    void bordersTest() {
        //Начало и конец
        memoryTaskManager.getTaskById(task1.getId());
        memoryTaskManager.getTaskById(task2.getId());
        memoryTaskManager.getEpicById(epic2.getId());
        memoryTaskManager.getSubtaskById(subtask1.getId());
        memoryTaskManager.getSubtaskById(subtask2.getId());


        memoryTaskManager.removeTaskById(task1.getId());
        assertEquals(List.of(task2, epic2, subtask1, subtask2), memoryTaskManager.getHistory());

        memoryTaskManager.removeSubtaskById(subtask2.getId());
        assertEquals(List.of(task2, epic2, subtask1), memoryTaskManager.getHistory());

        memoryTaskManager.removeEpicById(epic2.getId());
        assertEquals(List.of(task2, subtask1), memoryTaskManager.getHistory());

    }
}
