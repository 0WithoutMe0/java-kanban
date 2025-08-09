package taskmanager.tasks;

public class Subtask extends Task {


    private int epicId;

    public Subtask(String name, String description, Status status, TaskType type, int epicId) {
        super(name, description, status, type);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }



    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", epicId=" + epicId +
                '}';
    }
}