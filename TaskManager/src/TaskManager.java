import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;
    int countId = 0;

    TaskManager(){
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
    }

    public void getAllTasks(){
        String result = "";
        for(Task task : tasks.values()){
            result += task.toString() + '\'' + ", ";

        }

        System.out.println("Tasks[" + result + ']');
    }

    public void getAllEpics(){
        String result = "";
        for(Epic epic : epics.values()){
            result += epic.toString() + '\'' + ", ";
        }

        System.out.println("Epics[" + result + ']');
    }

    public void getAllSubtasks(){
        String result = "";
        for(Task subtask : subtasks.values()){
            result += subtask.toString() + '\'' + ", ";
        }

        System.out.println("Subtasks[" + result + ']');
    }

    public void removeAllTasks(){
        tasks.clear();
        System.out.println("Все средние задачи удалены!");
    }

    public void removeAllEpics(){
        for(Epic epic : epics.values()){
            epic.subtasks.clear();
        }
        epics.clear();
        System.out.println("Все большие задачи и их подзадачи удалены!");
    }

    public void removeAllSubtasks(){
        for(Subtask subtask : subtasks.values()){
            if(epics.containsKey(subtask.epicId)){
                epics.remove(subtask.epicId);
            }
        }
        subtasks.clear();
        System.out.println("Все подзадачи и их большие задачи удалены");
    }

    public void getTaskById(int id){
        if(tasks.containsKey(id)){
            System.out.println(tasks.get(id).toString());
        }else{
            System.out.println("Задачи с таким id не существует");
        }
    }

    public void getEpicById(int id){
        if(epics.containsKey(id)){
            System.out.println(epics.get(id).toString());
        }else{
            System.out.println("Большой задачи с таким id не существует");
        }
    }

    public void getSubtaskById(int id){
        if(subtasks.containsKey(id)){
            System.out.println(subtasks.get(id).toString());
        }else{
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    public void addTask(Task task){
        task.id = countId;
        tasks.put(countId++, task);
        System.out.println("Задача была успешно добавлена");
    }

    public void addEpic(Epic epic){
        epic.id = countId;
        epic.epicStatus(); //расчет статуса для эпика
        epics.put(countId++, epic);
        System.out.println("Большая задача была успешно добавлена");
    }

    public void addSubtask(Subtask subtask){
        subtask.id = countId;
        subtasks.put(countId++, subtask);
        epics.get(subtask.epicId).subtasks.add(subtask); //Внес подзадачу в эпик
        epics.get(subtask.epicId).epicStatus();
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
        epics.get(subtask.epicId).epicStatus();
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
            if(epics.get(subtasks.get(id).epicId).subtasks.isEmpty()){
                epics.remove(subtasks.get(id).epicId); // удаление пустого эпика
            }
            subtasks.remove(id);
            System.out.println("Подзадача была успешно удалена");
        }else{
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    public void getListOfEpic(int id){
        epics.get(id).listOfSubtasks();
    }

}
