import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.managers.TaskManager;
import taskmanager.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> extends DataForTest {
    T taskManager;

    protected abstract T createTaskManager();


    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        initializeCommonTestData(taskManager);
    }


    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, TaskType.MIDDLE_TASK);
        taskManager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(2), "Задачи не совпадают.");
    }


    @Test
    void removeAllTasks() {
        assertEquals(List.of(task1, task2), getTaskManager().getAllTasks());
        assertEquals(List.of(subtask1, subtask2), getTaskManager().getAllSubtasks());
        assertEquals(List.of(epic1, epic2), getTaskManager().getAllEpics());

        getTaskManager().removeAllTasks();
        getTaskManager().removeAllSubtasks();
        getTaskManager().removeAllEpics();

        assertEquals(List.of(), getTaskManager().getAllTasks());
        assertEquals(List.of(), getTaskManager().getAllSubtasks());
        assertEquals(List.of(), getTaskManager().getAllEpics());
    }

    @Test
    void taskShouldRemainUnchangedAfterAddingToManager() {
        assertEquals("Задача 1", task1.getName());
        assertEquals("ДЗ 1", task1.getDescription());
        assertEquals(Status.NEW, task1.getStatus());
        assertEquals(TaskType.MIDDLE_TASK, task1.getType());
    }



    public T getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(T taskManager) {
        this.taskManager = taskManager;
    }


}
