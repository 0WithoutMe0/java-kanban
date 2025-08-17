package taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {


    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, Status status, TaskType type, ArrayList<Subtask> subtasks) {
        super(name, description, status, type);
        this.subtasks = subtasks;
        this.getEndTime();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }



    public void updateEpicStatus() {
        int countDoneTasks = 0;
        boolean progressFlag = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.DONE) {
                countDoneTasks++;
            } else if (subtask.getStatus() == Status.IN_PROGRESS) {
                progressFlag = true;
                break;
            }
        }
        if (countDoneTasks == subtasks.size()) {
            setStatus(Status.DONE);
            System.out.println("Статус большой задачи DONE");
        } else if (progressFlag) {
            setStatus(Status.IN_PROGRESS);
            System.out.println("Статус большой задачи IN_PROGRESS");
        } else {
            setStatus(Status.NEW);
            System.out.println("Статус большой задачи NEW");
        }
        getEndTime(); //Решил в Update сделать инициализацию времени
    }


    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;
        Duration duration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            if (startTime == null || startTime.isAfter(subtask.getStartTime())) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null || endTime.isBefore(subtask.getStartTime())) {
                endTime = subtask.getStartTime();
            }
            duration.plus(subtask.getDuration());
        }
        if (startTime != null) {
            this.setStartTime(startTime);
        }

        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", subtasks.length=" + subtasks.size() +
                '}';
    }

}
