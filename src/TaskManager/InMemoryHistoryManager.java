package TaskManager;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final List<Task> tasksHistory;
    private final int HISTORY_LIMIT = 10;

    InMemoryHistoryManager(){
        tasksHistory = new LinkedList<>();
    }

    public void add(Task task){
        if(task == null){
            return;
        }
        tasksHistory.add(task);
        if(tasksHistory.size() > HISTORY_LIMIT){
            tasksHistory.remove(0);
        }
    }

    public List<Task> getHistory(){
         //список доступен только для чтения
         return List.copyOf(tasksHistory);
    }
}
