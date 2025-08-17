package taskmanager.managers;

import taskmanager.exeptions.TimeException;
import taskmanager.tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    private InMemoryHistoryManager historyManager;
    private int countId = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

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
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
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
    }

     @Override
     public void updateTask(Task task) {
        tasks.put(task.getId(), task);
     }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateEpicStatus();
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId()); //удаляю подзадачи
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtasks().remove(subtasks.get(id)); // удаляю подзадачу из эпика
            epics.get(subtasks.get(id).getEpicId()).updateEpicStatus();// Обнавляю статус эпика
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getListOfEpic(int id) {
        return epics.get(id).getSubtasks();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>((Task a, Task b) -> {
            if (a.getStartTime().isBefore(b.getStartTime())) {
                return 1;
            }
            return -1;
        });
        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
        for (Task task : subtasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
        return prioritizedTasks;
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

    public InMemoryHistoryManager getHistoryManager() {
        return historyManager;
    }
}
