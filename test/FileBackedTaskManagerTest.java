import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.managers.FileBackedTaskManager;
import taskmanager.managers.InMemoryTaskManager;
import taskmanager.managers.Managers;
import taskmanager.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Paths.get("temporary.txt"));
    }



    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager fileManager = Managers.getDefaultFileBacked(Paths.get("temporary_.txt"));
        fileManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(fileManager.getPath());

        Assertions.assertTrue(loadedManager.getAllTasks().isEmpty());
        Assertions.assertTrue(loadedManager.getAllEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {



        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(getTaskManager().getPath());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
        Assertions.assertEquals(2, loadedManager.getAllEpics().size());
        Assertions.assertEquals(2, loadedManager.getAllSubtasks().size());
    }

}

