import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Лаба 1", "Сделать лабу 1", Status.NEW, TaskType.MIDDLE_TASK);
        taskManager.addTask(task1);
        Task task2 = new Task("Лаба 2", "Сделать лабу 2", Status.NEW, TaskType.MIDDLE_TASK);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Ужин", "Приготовить ужин", Status.NEW, TaskType.EPIC, new ArrayList<>());
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Вуз", "Сходить", Status.NEW, TaskType.EPIC, new ArrayList<>());
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Картошка", "Сварить", Status.NEW, TaskType.SUBTASK, epic1.id);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Селедка", "Купить", Status.NEW, TaskType.SUBTASK, epic1.id);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Задача 1", "Проснуться", Status.NEW, TaskType.SUBTASK, epic2.id);
        taskManager.addSubtask(subtask3);


        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.getAllSubtasks();

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);
        taskManager.getAllEpics();

        taskManager.getListOfEpic(epic1.id);
        taskManager.getEpicById(epic2.id);
        taskManager.removeEpicById(epic1.id);
        taskManager.removeAllTasks();

        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.getAllSubtasks();
    }
}