public class Subtask extends Task{
    public int epicId;

    Subtask(String name, String description, Status status, TaskType type, int epicId){
        super(name, description, status, type);
        this.epicId = epicId;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;

    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", epicId=" + epicId +
                '}';
    }
}
