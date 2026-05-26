package pl.edu.pwr.simulation.tennispropath.model;

public enum RankingCategory {
    BRONZE(0, 999, "Bronze"),
    SILVER(1000, 1999, "Silver"),
    GOLD(2000, 2999, "Gold"),
    PLATINUM(3000, Integer.MAX_VALUE, "Platinum");

    private final int minPoints;
    private final int maxPoints;
    private final String displayName;

    RankingCategory(int minPoints, int maxPoints, String displayName) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.displayName = displayName;
    }

    public int getMinPoints() { return minPoints; }
    public int getMaxPoints() { return maxPoints; }
    public String getDisplayName() { return displayName; }

    public static RankingCategory fromPoints(int points) {
        for (RankingCategory category : values()) {
            if (points >= category.minPoints && points <= category.maxPoints) {
                return category;
            }
        }
        return BRONZE;
    }
}