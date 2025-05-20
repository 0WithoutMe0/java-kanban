import java.util.ArrayList;

public class Epic extends Task{
    public ArrayList<Subtask> subtasks;

    Epic(String name, String description, Status status, TaskType type, ArrayList<Subtask> subtasks){
        super(name, description, status, type);
        this.subtasks = subtasks;
    }

    public void updateEpicStatus(){
        int countDoneTasks = 0;
        boolean progressFlag = false;

        for(Subtask subtask : subtasks){
            if(subtask.status == Status.DONE){
                countDoneTasks++;
            }else if(subtask.status == Status.IN_PROGRESS){
                progressFlag = true;
                break;
            }
        }
        if(countDoneTasks == subtasks.size()){
            this.status = Status.DONE;
            System.out.println("Статус большой задачи DONE");
        }else if(progressFlag){
            this.status = Status.IN_PROGRESS;
            System.out.println("Статус большой задачи IN_PROGRESS");
        }else{
            this.status = Status.NEW;
            System.out.println("Статус большой задачи NEW");
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", subtasks.length=" + subtasks.size() +
                '}';
    }

}
