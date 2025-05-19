import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Subtask> subtasks;

    Epic(String name, String description, Status status, TaskType type, ArrayList<Subtask> subtasks){
        super(name, description, status, type);
        this.subtasks = subtasks;
    }

    public void epicStatus(){
        int countDoneTasks = 0;
        for(Subtask subtask : subtasks){
            if(subtask.status == Status.DONE){
                countDoneTasks++;
            }
        }
        if(countDoneTasks == subtasks.size()){
            this.status = Status.DONE;
            System.out.println("Статус большой задачи DONE");
        }else if(countDoneTasks == 0){
            this.status = Status.NEW;
            System.out.println("Статус большой задачи NEW");
        }else{
            this.status = Status.IN_PROGRESS;
            System.out.println("Статус большой задачи IN_PROGRESS");
        }
    }

    public void listOfSubtasks(){
        System.out.println(subtasks);
    }

}
