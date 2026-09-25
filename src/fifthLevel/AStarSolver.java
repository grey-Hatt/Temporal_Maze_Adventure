package fifthLevel;

import java.util.*;

public class AStarSolver {
    public static class Result {
        public final int distance;
        public final List<GameLogic.Position> path;
        public Result(int distance, List<GameLogic.Position> path) { this.distance = distance; this.path = path; }
    }

    // A* on grid using Manhattan heuristic. Uses PriorityQueue + HashSet/HashMap.
    public static Result solve(int[][] maze, int sr, int sc, int tr, int tc) {
        int rows = maze.length, cols = maze[0].length;
        int startKey = sr * cols + sc;
        int targetKey = tr * cols + tc;

        Map<Integer, Integer> gScore = new HashMap<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();

        // PQ entries: {f, key, g}
        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.<int[]>comparingInt(a -> a[0]).thenComparingInt(a -> a[2]));
        Set<Integer> closed = new HashSet<>();

        int h0 = Math.abs(sr - tr) + Math.abs(sc - tc);
        gScore.put(startKey, 0);
        open.add(new int[] { h0, startKey, 0 });

        int[] dr = {-1,1,0,0};
        int[] dc = {0,0,-1,1};

        while (!open.isEmpty()) {
            int[] curEntry = open.poll();
            int current = curEntry[1];
            int curG = curEntry[2];
            if (closed.contains(current)) continue;
            if (current == targetKey) break;
            closed.add(current);

            int r = current / cols, c = current % cols;
            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k], nc = c + dc[k];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (maze[nr][nc] == GameLogic.WALL) continue;
                int nbKey = nr * cols + nc;
                if (closed.contains(nbKey)) continue;
                int tentativeG = curG + 1;
                if (tentativeG < gScore.getOrDefault(nbKey, Integer.MAX_VALUE)) {
                    cameFrom.put(nbKey, current);
                    gScore.put(nbKey, tentativeG);
                    int f = tentativeG + Math.abs(nr - tr) + Math.abs(nc - tc);
                    open.add(new int[] { f, nbKey, tentativeG });
                }
            }
        }

        if (!gScore.containsKey(targetKey) && startKey != targetKey) {
            return new Result(-1, Collections.emptyList());
        }

        LinkedList<GameLogic.Position> path = new LinkedList<>();
        if (startKey == targetKey) {
            path.add(new GameLogic.Position(sr, sc));
            return new Result(0, path);
        }
        int cur = targetKey;
        // if target was never reached in cameFrom chain, but gScore contains target, still try reconstruct
        path.addFirst(new GameLogic.Position(cur / cols, cur % cols));
        while (cur != startKey) {
            Integer prev = cameFrom.get(cur);
            if (prev == null) break;
            cur = prev;
            path.addFirst(new GameLogic.Position(cur / cols, cur % cols));
        }
        int dist = path.size() - 1;
        return new Result(dist, path);
    }
}
