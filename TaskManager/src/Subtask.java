public class Subtask extends Task{
    int epicId;

    Subtask(String name, String description, Status status, TaskType type, int epicId){
        super(name, description, status, type);
        this.epicId = epicId;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
