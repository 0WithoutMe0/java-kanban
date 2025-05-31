import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> tasksHistory;
    private static final int HISTORY_LIMIT = 10;

    InMemoryHistoryManager(){
        tasksHistory = new LinkedList<>();
    }

    public void add(Task task){
        tasksHistory.add(task);
        if(tasksHistory.size() > HISTORY_LIMIT){
            tasksHistory.remove(0);
        }
    }

    public List<Task> getHistory(){
        return tasksHistory;
    }
}
