package pl.edu.pwr.simulation.tennispropath.model;

/**
 * Typ wyliczeniowy (Enum) reprezentujący rodzaje nawierzchni kortów tenisowych w grze
 * Każda z nawierzchni posiada unikalną nazwę wyświetlaną w interfejsie graficznym
 * oraz krótki opis definiujący jej charakterystykę i wpływ na styl rozgrywki.
 */
public enum CourtType {
    // Definicje stałych enum odpowiadających trzem głównym typom nawierzchni w zawodowym tenisie
    CLAY("Mączka", "Premiuje zawodników z dużą wytrzymałością"),
    GRASS("Trawa", "Drastycznie premiuje serwis"),
    HARD("Twarda", "Najbardziej zbalansowana nawierzchnia");

    // Pola przechowujące atrybuty informacyjne i wizualne dla każdej nawierzchni
    private final String displayName;
    private final String description;

    /**
     * Wewnętrzny konstruktor enuma parujący stałe z ich opisami wizualnymi i mechanicznymi
     *
     * @param displayName Nazwa wyświetlana w interfejsie
     * @param description Charakterystyka wpływu kortu na symulację meczu
     */
    CourtType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // ====================================================================
    // GETTERY
    // ====================================================================

    /**
     * Pobiera sformatowaną nazwę nawierzchni (np. na potrzeby metody updateUI w kontrolerze)
     */
    public String getDisplayName() { return displayName; }

    /**
     * Pobiera opis tekstowy cech charakterystycznych dla danego typu kortu
     */
    public String getDescription() { return description; }
}