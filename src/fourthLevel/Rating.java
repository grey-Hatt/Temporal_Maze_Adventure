package fourthLevel;

public class Rating {
    // Returns stars: 5, 3, or 2
    // playerSteps = number of steps taken (edges), shortestSteps = shortest path length (edges)
    public static int computeStars(int shortestSteps, int playerSteps) {
        if (shortestSteps <= 0 || playerSteps <= 0) return 2;
        if (playerSteps <= shortestSteps) return 5; // optimal
        // within 50% of optimal -> 3
        if (playerSteps <= Math.ceil(shortestSteps * 1.5)) return 3;
        return 2;
    }

    public static String starsString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) sb.append("★");
        for (int i = stars; i < 5; i++) sb.append('☆');
        return sb.toString();
    }
}
