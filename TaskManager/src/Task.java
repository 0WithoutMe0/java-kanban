public class Task {
    public String name;
    public String description;
    public  int id;
    public Status status;
    public TaskType type;


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
    public int hashCode() {
        return this.id;
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
