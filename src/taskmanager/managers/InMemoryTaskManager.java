package taskmanager.managers;

import taskmanager.exeptions.TimeException;
import taskmanager.tasks.*;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    private final Map<Integer, Epic> epics = new HashMap<Integer, Epic>();
    private final Map<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private TreeSet<Task> prioritizedTasks = new TreeSet<>((Task a, Task b) -> {
        return a.getStartTime().isBefore(b.getStartTime()) ? 1 : -1;
    });
    private int countId = 0;



    public int getCountId() {
        return countId;
    }

    public void setCountId(int countId) {
        this.countId = countId;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        prioritizedTasks.removeAll(subtasks.values());
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId()); // удалить подзадачи из HashMap<Integer, Subtask> subtasks
            }
            epic.getSubtasks().clear();
        }
        epics.clear();

    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateEpicStatus();
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        if (isCrossedWithAllTasks(task)) {
            throw new TimeException("Задача пересекается по времени с существующими");
        }
        if (task.getId() < 0) {
            task.setId(countId++);
        }
        tasks.put(task.getId(), task);
        if (!task.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (isCrossedWithAllTasks(epic)) {
            throw new TimeException("Задача пересекается по времени с существующими");
        }
        if (epic.getId() < 0) {
            epic.setId(countId++);
        }
        epic.updateEpicStatus(); //расчет статуса для эпика
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) throws RuntimeException {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new RuntimeException("Указан несуществующий эпик");
        }
        if (isCrossedWithAllTasks(subtask)) {
            throw new TimeException("Задача пересекается по времени с существующими");
        }
        if (subtask.getId() < 0) {
            subtask.setId(countId++);
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask); //Внес подзадачу в эпик
        epics.get(subtask.getEpicId()).updateEpicStatus();
        if (!subtask.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(subtask);
        }
    }

     @Override
     public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        if (!task.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
        }
     }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateEpicStatus();
        if (!subtask.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            historyManager.remove(id);
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpicById(int id) {
        prioritizedTasks.removeAll(epics.get(id).getSubtasks());
        if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId()); //удаляю подзадачи
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtasks().remove(subtasks.get(id)); // удаляю подзадачу из эпика
            epics.get(subtasks.get(id).getEpicId()).updateEpicStatus();// Обнавляю статус эпика
            prioritizedTasks.remove(subtasks.get(id));
            historyManager.remove(id);
            subtasks.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getListOfEpic(int id) {
        return epics.get(id).getSubtasks();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isCrossed(Task task1, Task task2) {
        if (task1.getStartTime().equals(LocalDateTime.MIN) || task2.getStartTime().equals(LocalDateTime.MIN)) {
            return false;
        }
        return task2.getStartTime().isBefore(task1.getEndTime())
                && task1.getStartTime().isBefore(task2.getEndTime());
    }

    public boolean isCrossedWithAllTasks(Task checkTask) {
        for (Task task : tasks.values()) {
            if (isCrossed(checkTask, task)) {
                return true;
            }
        }
        for (Task subtask : subtasks.values()) {
            if (isCrossed(checkTask, subtask)) {
                return true;
            }
        }
        return false;
    }

}
