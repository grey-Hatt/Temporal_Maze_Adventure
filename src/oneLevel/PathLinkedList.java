package oneLevel;

/**
 * Doubly linked list storing the player's path.
 */
public class PathLinkedList {
    public static class Node {
        public int row, col;
        public Node next, prev;
        public Node(int r, int c) { row = r; col = c; next = prev = null; }
    }

    private Node head, tail;
    private int size;

    public PathLinkedList() { clear(); }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    public void addMove(int r, int c) {
        Node n = new Node(r, c);
        if (head == null) {
            head = tail = n;
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
        size++;
    }

    public Node getHead() { return head; }
    public Node getTail() { return tail; }
    public int size() { return size; }

    public void removeLast() {
        if (tail == null) return;
        if (tail.prev == null) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size = Math.max(0, size - 1);
    }
}
