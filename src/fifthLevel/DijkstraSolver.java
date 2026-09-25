package fifthLevel;

import java.util.*;

// Dijkstra on grid (treat non-WALL as passable). Returns distance and path.
public class DijkstraSolver {
    public static class Result {
        public final int distance; // number of steps from start to target
        public final List<GameLogic.Position> path; // inclusive start..target

        public Result(int distance, List<GameLogic.Position> path) {
            this.distance = distance;
            this.path = path;
        }
    }

    public static Result solve(int[][] maze, int sr, int sc, int tr, int tc) {
        int rows = maze.length;
        int cols = maze[0].length;
        int[][] dist = new int[rows][cols];
        for (int i = 0; i < rows; i++) Arrays.fill(dist[i], Integer.MAX_VALUE);
        GameLogic.Position[][] prev = new GameLogic.Position[rows][cols];

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        // entries: {distance, r, c}
        dist[sr][sc] = 0;
        pq.add(new int[]{0, sr, sc});

        int[] dr = {-1,1,0,0};
        int[] dc = {0,0,-1,1};

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], r = cur[1], c = cur[2];
            if (d != dist[r][c]) continue;
            if (r == tr && c == tc) break;
            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k], nc = c + dc[k];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int cell = maze[nr][nc];
                if (cell == GameLogic.WALL) continue;
                int nd = d + 1; // uniform cost
                if (nd < dist[nr][nc]) {
                    dist[nr][nc] = nd;
                    prev[nr][nc] = new GameLogic.Position(r, c);
                    pq.add(new int[]{nd, nr, nc});
                }
            }
        }

        if (dist[tr][tc] == Integer.MAX_VALUE) {
            return new Result(-1, Collections.emptyList());
        }

        // reconstruct path
        LinkedList<GameLogic.Position> path = new LinkedList<>();
        int r = tr, c = tc;
        while (!(r == sr && c == sc)) {
            path.addFirst(new GameLogic.Position(r, c));
            GameLogic.Position p = prev[r][c];
            if (p == null) break; // safety
            r = p.r; c = p.c;
        }
        path.addFirst(new GameLogic.Position(sr, sc));
        return new Result(dist[tr][tc], path);
    }
}
