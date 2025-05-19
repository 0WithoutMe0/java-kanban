public class Task {
    String name;
    String description;
    int id;
    Status status;
    TaskType type;


    Task(String name, String description, Status status, TaskType type){
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
