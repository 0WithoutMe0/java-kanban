import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.FileBackedTaskManager;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManagerTest {

    FileBackedTaskManager fileManager;
    Task task1;
    Task task2;
    Subtask subtask1;
    Subtask subtask2;
    Epic epic1;
    Epic epic2;

    @BeforeEach
    void beforeEach() throws IOException {
        fileManager = Managers.getDefaultFileBacked(Files.createTempFile("temporary", ".txt"));
    }

    @Test
    void shouldLoadEmptyFile() {
        fileManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(fileManager.getPath());

        Assertions.assertTrue(loadedManager.getAllTasks().isEmpty());
        Assertions.assertTrue(loadedManager.getAllEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        task1 = new Task("Задача 1", "ДЗ 1", Status.NEW, TaskType.MIDDLE_TASK);
        fileManager.addTask(task1);
        task2 = new Task("Задача 2", "ДЗ 2", Status.NEW, TaskType.MIDDLE_TASK);
        fileManager.addTask(task2);
        epic1 = new Epic("Эпик 1", "ДЗ 1", Status.NEW, TaskType.EPIC, new ArrayList<>());
        fileManager.addEpic(epic1);
        epic2 = new Epic("Эпик 2", "ДЗ 2", Status.NEW, TaskType.EPIC, new ArrayList<>());
        fileManager.addEpic(epic2);
        subtask1 = new Subtask("Субзадача 1", "ДЗ 1", Status.NEW, TaskType.SUBTASK, epic1.getId());
        fileManager.addSubtask(subtask1);
        subtask2 = new Subtask("Субзадача 2", "ДЗ 2", Status.NEW, TaskType.SUBTASK, epic2.getId());
        fileManager.addSubtask(subtask2);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(fileManager.getPath());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
        Assertions.assertEquals(2, loadedManager.getAllEpics().size());
        Assertions.assertEquals(2, loadedManager.getAllSubtasks().size());
    }

}

