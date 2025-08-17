package taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {


    private int epicId;

    public Subtask(String name, String description, Status status, TaskType type, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, type, startTime, duration);
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