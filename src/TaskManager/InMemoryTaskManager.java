package TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    private InMemoryHistoryManager historyManager;
    private int countId = 0;

    InMemoryTaskManager(){
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    int getCountId(){
        return countId;
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for(Task task : tasks.values()){
            listOfTasks.add(task);
        }

        return listOfTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics(){
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for(Epic epic : epics.values()){
            listOfEpics.add(epic);
        }

        return listOfEpics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for(Subtask subtask : subtasks.values()){
            listOfSubtasks.add(subtask);
        }

        return listOfSubtasks;
    }

    @Override
    public void removeAllTasks(){
        tasks.clear();
    }

    @Override
    public void removeAllEpics(){
        for(Epic epic : epics.values()){
            for(Subtask subtask : epic.getSubtasks()){
                subtasks.remove(subtask.getId()); // удалить подзадачи из HashMap<Integer, Subtask> subtasks
            }
            epic.getSubtasks().clear();
        }
        epics.clear();
    }

    @Override
    public void removeAllSubtasks(){
        for(Epic epic : epics.values()){
            epic.getSubtasks().clear();
            epic.updateEpicStatus();
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id){
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id){
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id){
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void addTask(Task task){
        task.setId(countId);
        tasks.put(countId++, task);
    }

    @Override
    public void addEpic(Epic epic){
        epic.setId(countId);
        epic.updateEpicStatus(); //расчет статуса для эпика
        epics.put(countId++, epic);
    }

    @Override
    public void addSubtask(Subtask subtask){
        subtask.setId(countId);
        subtasks.put(countId++, subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask); //Внес подзадачу в эпик
        epics.get(subtask.getEpicId()).updateEpicStatus();
    }

     @Override
     public void updateTask(Task task){
         tasks.put(task.getId(), task);
     }

    @Override
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateEpicStatus();
    }

    @Override
    public void removeTaskById(int id){
        if(tasks.containsKey(id)){
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpicById(int id){
        if(epics.containsKey(id)){
            for(Subtask subtask : epics.get(id).getSubtasks()){
                subtasks.remove(subtask.getId()); //удаляю подзадачи
            }
            epics.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id){
        if(subtasks.containsKey(id)){
            epics.get(subtasks.get(id).getEpicId()).getSubtasks().remove(subtasks.get(id)); // удаляю подзадачу из эпика
            epics.get(subtasks.get(id).getEpicId()).updateEpicStatus();// Обнавляю статус эпика
            subtasks.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getListOfEpic(int id){
        return epics.get(id).getSubtasks();
    }

    List<Task> getHistory(){
        return historyManager.getHistory();
    }

}
