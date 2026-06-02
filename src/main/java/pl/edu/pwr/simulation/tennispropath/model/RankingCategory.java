package pl.edu.pwr.simulation.tennispropath.model;

/**
 * Typ wyliczeniowy (Enum) reprezentujący kategorie (tiery) rankingowe w Symulacji
 * definiuje sztywne przedziały punktów ATP wymagane do osiągnięcia danej rangi
 * oraz przechowuje czytelne nazwy wyświetlane w interfejsie użytkownika
 */
public enum RankingCategory {
    // Definicje stałych enum wraz z przypisanymi progami punktowymi [min, max] oraz nazwą wyświetlaną
    BRONZE(0, 999, "Bronze"),
    SILVER(1000, 1999, "Silver"),
    GOLD(2000, 2999, "Gold"),
    PLATINUM(3000, Integer.MAX_VALUE, "Platinum"); // Klasa mistrzowska - od 3000 punktów do nieskończoności

    // Pola przechowujące atrybuty każdej kategorii rankingowej
    private final int minPoints;       // Minimalna liczba punktów potrzebna do utrzymania/zdobycia rangi
    private final int maxPoints;       // Maksymalna liczba punktów w danym przedziale rangi
    private final String displayName;  // Tekstowa nazwa kategorii wykorzystywana w GUI

    /**
     * Wewnętrzny konstruktor enuma przypisujący parametry do konkretnych rang.
     * @param minPoints   Dolna granica punktowa
     * @param maxPoints   Górna granica punktowa
     * @param displayName Nazwa wizualna
     */
    RankingCategory(int minPoints, int maxPoints, String displayName) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.displayName = displayName;
    }

    // ====================================================================
    // GETTERY
    // ====================================================================

    public int getMinPoints() { return minPoints; }
    public int getMaxPoints() { return maxPoints; }
    public String getDisplayName() { return displayName; }

    /**
     * Statyczna metoda narzędziowa automatycznie dopasowująca kategorię rankingową
     * na podstawie przesłanej liczby punktów ATP zawodnika
     *
     * @param points Aktualny dorobek punktowy tenisisty
     * @return Dopasowany obiekt RankingCategory (w przypadku błędu lub braku dopasowania zwraca BRONZE)
     */
    public static RankingCategory fromPoints(int points) {
        // Przebieganie przez wszystkie zdefiniowane wyżej wartości enuma (BRONZE, SILVER, GOLD, PLATINUM)
        for (RankingCategory category : values()) {
            // Sprawdzenie, czy przesłana liczba punktów mieści się w widłkach [min, max] danej kategorii
            if (points >= category.minPoints && points <= category.maxPoints) {
                return category; // Zwrócenie pasującej rangi i natychmiastowe przerwanie pętli
            }
        }
        // Zabezpieczenie na wypadek podania wartości skrajnych lub nieprzewidzianych
        return BRONZE;
    }
}