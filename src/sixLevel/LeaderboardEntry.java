package sixLevel;

/**
 * Represents a leaderboard entry with score, time taken, and steps.
 * Implements Comparable to sort by score descending, then time ascending, then steps ascending.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private final int score;
    private final int time;
    private final int steps;

    public LeaderboardEntry(int score, int time, int steps) {
        this.score = score;
        this.time = time;
        this.steps = steps;
    }

    public int getScore() {
        return score;
    }

    public int getTime() {
        return time;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Sort by score descending
        if (this.score != other.score) {
            return Integer.compare(other.score, this.score);
        }
        // If scores equal, sort by time ascending
        if (this.time != other.time) {
            return Integer.compare(this.time, other.time);
        }
        // If time equal, sort by steps ascending
        return Integer.compare(this.steps, other.steps);
    }

    @Override
    public String toString() {
        return String.format("Score: %d, Time: %d, Steps: %d", score, time, steps);
    }
}
