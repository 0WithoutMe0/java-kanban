package taskmanager.managers;


import taskmanager.tasks.*;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node> nodeMap;
    private Node head;
    private Node tail;


    InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
    }

    public void linkLast(Task task) {
        Node node = nodeMap.remove(task.getId());
        if (node != null) {
            removeNode(node);
        }
        Node newNode = new Node(task, tail, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> arr = new ArrayList<>();
        Node current = head;
        while (current != null) {
            arr.add(current.getTask());
            current = current.getNext();
        }
        return arr;
    }

    void removeNode(Node node) {
        if (head == null) {
            return;
        }

        if (node.equals(head)) {
            head = head.getNext();
            if (head != null) {
                head.setPrev(null);
            } else {
                tail = null;
            }
        } else if (node.equals(tail)) {
            tail = tail.getPrev();
            tail.setNext(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
    }


    public void remove(int id) {
        removeNode(nodeMap.get(id));
        nodeMap.remove(id);
    }

    public List<Task> getHistory() {
         //список доступен только для чтения
         return getTasks();
    }
}
