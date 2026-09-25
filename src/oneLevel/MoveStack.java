package oneLevel;

import java.util.Stack;

/**
 * Stack wrapper used to store positions for undo feature.
 */
public class MoveStack {
    private final Stack<Position> stack = new Stack<>();

    public void push(Position p) {
        // push a copy to avoid aliasing
        stack.push(new Position(p.row, p.col));
    }

    public Position pop() {
        if (stack.isEmpty()) return null;
        Position p = stack.pop();
        return new Position(p.row, p.col);
    }

    public Position peek() {
        if (stack.isEmpty()) return null;
        Position p = stack.peek();
        return new Position(p.row, p.col);
    }

    public int size() { return stack.size(); }
    public void clear() { stack.clear(); }
}
