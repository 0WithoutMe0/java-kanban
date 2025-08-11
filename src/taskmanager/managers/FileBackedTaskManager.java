package taskmanager.managers;

import taskmanager.exeptions.ManagerSaveException;
import taskmanager.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public void save() {
        ArrayList<Task> tasks = super.getAllTasks();
        ArrayList<Epic> epics = super.getAllEpics();
        ArrayList<Subtask> subtasks = super.getAllSubtasks();

        try {
            Files.write(path, "id,type,name,status,description,epic\n".getBytes());
            for (Task task : tasks) {
                Files.write(path, toString(task).getBytes(), StandardOpenOption.APPEND);
            }
            for (Epic task : epics) {
                Files.write(path, toString(task).getBytes(), StandardOpenOption.APPEND);
            }
            for (Subtask task : subtasks) {
                Files.write(path, toString(task).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось сделать запись в файл");
        }
    }

    String toString(Task task) {
        String stringEpicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            stringEpicId = String.valueOf((subtask.getEpicId()));
        }
        return task.getId() + "," + task.getType().name() + "," + task.getName() +
                "," + task.getStatus().name() + "," + task.getDescription() + "," + stringEpicId + "\n";
    }

    static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        if (type == TaskType.MIDDLE_TASK) {
            Task task = new Task(name, description, status, type);
            task.setId(id);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(name, description, status, type, new ArrayList<>());
            epic.setId(id);
            return epic;
        }
        int epicId = Integer.parseInt(split[5]);
        Subtask subtask = new Subtask(name, description, status, type, epicId);
        subtask.setId(id);
        return subtask;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxCountId = 0;
        try {
            List<String> dataLines = Files.readAllLines(file);
            for (int i = 1; i < dataLines.size(); i++) {
                Task task = fromString(dataLines.get(i));
                maxCountId = Math.max(maxCountId, task.getId());
                if (task.getType() == TaskType.MIDDLE_TASK) {
                    manager.addTask(task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.addEpic((Epic) task);
                } else {
                    manager.addSubtask((Subtask) task);
                }
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось прочитать файл");
        }
        manager.setCountId(maxCountId);
        return manager;
    }
}