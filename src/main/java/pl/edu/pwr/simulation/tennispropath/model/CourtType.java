package pl.edu.pwr.simulation.tennispropath.model;

public enum CourtType {
    CLAY("Mączka (Ceglana)", "Premiuje zawodników z dużą wytrzymałością"),
    GRASS("Trawa", "Drastycznie premiuje atomowy serwis"),
    HARD("Twarda (Beton)", "Najbardziej zbalansowana nawierzchnia");

    private final String displayName;
    private final String description;

    CourtType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}