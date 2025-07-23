package TaskManager.Managers;



import TaskManager.Managers.HistoryManager;
import TaskManager.Tasks.*;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node> nodeMap;
    private Node head;
    private Node tail;


    InMemoryHistoryManager(){
        nodeMap = new HashMap<>();
    }

    public void linkLast(Task task){
        if(nodeMap.containsKey(task.getId())){
            removeNode(nodeMap.get(task.getId()));
            nodeMap.remove(task.getId());
        }
        Node newNode = new Node(task, tail, null);
        if(tail == null){
            head = newNode;
        }else{
            tail.next = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks(){
        List<Task> arr = new ArrayList<>();
        Node current = head;
        while(current != null){
            arr.add(current.task);
            current = current.next;
        }
        return arr;
    }

    void removeNode(Node node){
        if(head == null){
            return;
        }

        if(node.equals(head)){
            head = head.next;
            if(head != null){
                head.prev = null;
            }else{
                tail = null;
            }
        }else if(node.equals(tail)){
            tail = tail.prev;
            tail.next = null;
        }else{
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public void add(Task task){
        if(task == null){
            return;
        }
        linkLast(task);
    }


    public void remove(int id) {
        removeNode(nodeMap.get(id));
    }

    public List<Task> getHistory(){
         //список доступен только для чтения
         return getTasks();
    }
}
