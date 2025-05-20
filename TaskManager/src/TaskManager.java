import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Epic> epics;
    public HashMap<Integer, Subtask> subtasks;
    int countId = 0;

    TaskManager(){
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
    }

    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for(Task task : tasks.values()){
            listOfTasks.add(task);
        }

        return listOfTasks;
    }

    public ArrayList<Epic> getAllEpics(){
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for(Epic epic : epics.values()){
            listOfEpics.add(epic);
        }

        return listOfEpics;
    }

    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for(Subtask subtask : subtasks.values()){
            listOfSubtasks.add(subtask);
        }

        return listOfSubtasks;
    }

    public void removeAllTasks(){
        tasks.clear();
        System.out.println("Все средние задачи удалены!");
    }

    public void removeAllEpics(){
        for(Epic epic : epics.values()){
            for(Subtask subtask : epic.subtasks){
                subtasks.remove(subtask.id); // удалить подзадачи из HashMap<Integer, Subtask> subtasks
            }
            epic.subtasks.clear();
        }
        epics.clear();
        System.out.println("Все большие задачи и их подзадачи удалены!");
    }

    public void removeAllSubtasks(){
        for(Epic epic : epics.values()){
            epic.subtasks.clear();
            epic.updateEpicStatus();
        }
        subtasks.clear();
        System.out.println("Все подзадачи  удалены");
    }

    public Task getTaskById(int id){
        if(tasks.containsKey(id)){
            return tasks.get(id);
        }else{
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    public Epic getEpicById(int id){
        if(epics.containsKey(id)){
            return epics.get(id);
        }else{
            System.out.println("Большой задачи с таким id не существует");
            return null;
        }
    }

    public Subtask getSubtaskById(int id){
        if(subtasks.containsKey(id)){
            return subtasks.get(id);
        }else{
            System.out.println("Подзадачи с таким id не существует");
            return null;
        }
    }

    public void addTask(Task task){
        task.id = countId;
        tasks.put(countId++, task);
        System.out.println("Задача была успешно добавлена");
    }

    public void addEpic(Epic epic){
        epic.id = countId;
        epic.updateEpicStatus(); //расчет статуса для эпика
        epics.put(countId++, epic);
        System.out.println("Большая задача была успешно добавлена");
    }

    public void addSubtask(Subtask subtask){
        subtask.id = countId;
        subtasks.put(countId++, subtask);
        epics.get(subtask.epicId).subtasks.add(subtask); //Внес подзадачу в эпик
        epics.get(subtask.epicId).updateEpicStatus();
        System.out.println("Подзадача была успешно добавлена");
    }

     public void updateTask(Task task){
         tasks.put(task.id, task);
         System.out.println("Задача была обновлена");
     }

    public void updateEpic(Epic epic){
        epics.put(epic.id, epic);
        System.out.println("Большая задача была обновлена");
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.id, subtask);
        epics.get(subtask.epicId).updateEpicStatus();
        System.out.println("Подзадача была обновлена");
    }

    public void removeTaskById(int id){
        if(tasks.containsKey(id)){
            tasks.remove(id);
            System.out.println("Задача была успешно удалена");
        }else{
            System.out.println("Задачи с таким id не существует");
        }
    }

    public void removeEpicById(int id){
        if(epics.containsKey(id)){
            for(Subtask subtask : epics.get(id).subtasks){
                subtasks.remove(subtask.id); //удаляю подзадачи
            }
            epics.remove(id);
            System.out.println("Большая задача была успешно удалена");
        }else{
            System.out.println("Большой задачи с таким id не существует");
        }
    }

    public void removeSubtaskById(int id){
        if(subtasks.containsKey(id)){
            epics.get(subtasks.get(id).epicId).subtasks.remove(subtasks.get(id)); // удаляю подзадачу из эпика
            epics.get(subtasks.get(id).epicId).updateEpicStatus();// Обнавляю статус эпика
            subtasks.remove(id);
            System.out.println("Подзадача была успешно удалена");
        }else{
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    public ArrayList<Subtask> getListOfEpic(int id){
        return epics.get(id).subtasks;
    }

}
