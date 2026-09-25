package oneLevel;

import java.util.LinkedList;
import java.util.Queue;



public class ReplayQueue {
    private final Queue<Position> q = new LinkedList<>();

    public void enqueue(Position p) { q.add(new Position(p.row, p.col)); }
    public Position dequeue() {
        Position p = q.poll();
        return p == null ? null : new Position(p.row, p.col);
    }
    public void clear() { q.clear(); }
    public boolean isEmpty() { return q.isEmpty(); }
    public int size() { return q.size(); }
}
