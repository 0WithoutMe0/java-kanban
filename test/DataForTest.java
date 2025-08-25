import taskmanager.managers.TaskManager;
import taskmanager.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class DataForTest {
    protected Task task1;
    protected Task task2;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Epic epic1;
    protected Epic epic2;

    protected void initializeCommonTestData(TaskManager manager) {
        task1 = new Task("Task1", "Homework 1", Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task1);
        task2 = new Task("Задача 2", "ДЗ 2", Status.NEW, TaskType.MIDDLE_TASK, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
        manager.addTask(task2);
        epic1 = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        manager.addEpic(epic1);
        epic2 = new Epic("Эпик 2", "ДЗ 2", Status.NEW, TaskType.EPIC, new ArrayList<>());
        manager.addEpic(epic2);
        subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic1.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        manager.addSubtask(subtask1);
        subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic2.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        manager.addSubtask(subtask2);
    }
}