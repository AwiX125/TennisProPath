package pl.edu.pwr.simulation.tennispropath.model;

/**
 * Klasa abstrakcyjna stanowiąca wspólny fundament (klasę bazową) dla każdego tenisisty w systemie
 * Definiuje kluczowe atrybuty uderzeń, adaptacji do poszczególnych nawierzchni, dane profilowe
 * oraz system punktacji rankingowej ATP. Te wspólne pola i zachowania są dziedziczone zarówno
 * przez gracza (Player), jak i przez autonomiczne boty (Opponent).
 */
public abstract class TennisPlayer {

    // ====================================================================
    // ATRYBUTY TECHNICZNE I ADAPTACYJNE
    // ====================================================================

    // Atrybuty czysto tenisowe, określające bazową siłę uderzeń (skala od 1 do 100)
    protected double forehandStrength; // Siła, precyzja i powtarzalność zagrania z forehandu
    protected double backhandStrength; // Siła, precyzja i stabilność zagrania z backhandu
    protected double serveStrength;    // Efektywność, asy serwisowe oraz siła serwisu zawodnika

    // Atrybuty specyfiki nawierzchni, określające współczynnik adaptacji (skala od 1 do 100)
    protected double clayStrength;     // Efektywność gry na mączce ceglastej (korty ziemne / wolne)
    protected double grassStrength;    // Efektywność gry na trawie (korty naturalne / bardzo szybkie)
    protected double hardStrength;     // Efektywność gry na betonie (korty twarde / uniwersalne)

    // ====================================================================
    // PRYWATNE METRYKI PROFILU I RANKINGU
    // ====================================================================

    // Podstawowe dane profilowe i metryki rozwoju zawodnika
    protected String name;           // Imię i nazwisko (lub pseudonim generowany dla botów)
    protected int age;               // Aktualny wiek tenisisty wyrażony w latach
    protected int skillLevel;        // Ogólny poziom umiejętności (Skill), determinujący klasę gracza
    private CourtType favoriteCourt; // Preferowany, ulubiony typ nawierzchni kortu tenisowego

    // System światowego rankingu turniejowego ATP
    private int rankingPoints;       // Zgromadzone punkty rankingowe, decydujące o pozycji w tabeli ligowej

    /**
     * Konstruktor klasy abstrakcyjnej
     * Inicjalizuje podstawowe dane tożsamościowe oraz fizyczne zawodnika podczas jego kreacji
     *
     * @param name        Imię i nazwisko gracza
     * @param skillLevel  Startowy ogólny poziom umiejętności (Skill)
     * @param age         Startowy wiek zawodnika
     */
    public TennisPlayer(String name, int skillLevel, int age) {
        this.name = name;
        this.age = age;
        this.skillLevel = skillLevel;
    }

    /**
     * Zwiększa wiek zawodnika o 1 rok
     * Wywoływane najczęściej przy cyklicznych aktualizacjach kalendarza (np. podczas rocznej ewolucji touru co 52 tygodnie).
     */
    public void ageUp() {
        this.age++;
    }

    // ====================================================================
    // GETTERY I SETTERY
    // ====================================================================

    // --- Atrybuty techniczne i fizyczne (uderzenia oraz korty) ---

    /**
     * Pobiera siłę forehandu zawodnika.
     * @return wartość forehandStrength
     */
    public double getForehandStrength() { return forehandStrength; }

    /**
     * Pobiera siłę backhandu zawodnika.
     * @return wartość backhandStrength
     */
    public double getBackhandStrength() { return backhandStrength; }

    /**
     * Pobiera siłę serwisu zawodnika.
     * @return wartość serveStrength
     */
    public double getServeStrength()    { return serveStrength; }

    /**
     * Pobiera adaptację zawodnika do gry na mączce.
     * @return wartość clayStrength
     */
    public double getClayStrength()     { return clayStrength; }

    /**
     * Pobiera adaptację zawodnika do gry na trawie.
     * @return wartość grassStrength
     */
    public double getGrassStrength()    { return grassStrength; }

    /**
     * Pobiera adaptację zawodnika do gry na betonie.
     * @return wartość hardStrength
     */
    public double getHardStrength()     { return hardStrength; }

    /**
     * Pobiera preferowany typ nawierzchni zawodnika.
     * @return ulubiony typ kortu
     */
    public CourtType getFavoriteCourt() { return favoriteCourt; }

    /**
     * Mapuje przesłany typ nawierzchni na konkretną statystykę siły adaptacji zawodnika
     *
     * @param court Typ kortu (wartość z enuma CourtType)
     * @return Umiejętności (współczynnik siły) na odpowiadającym typie kortu
     */
    public double getStrengthForCourt(CourtType court) {
        return switch (court) {
            case CLAY -> this.clayStrength;
            case GRASS -> this.grassStrength;
            case HARD -> this.hardStrength;
        };
    }

    // --- Dane identyfikacyjne i profil zawodnika ---

    /**
     * Pobiera imię lub pseudonim tenisisty.
     * @return nazwa zawodnika
     */
    public String getName()    { return name; }

    /**
     * Pobiera ogólny poziom umiejętności zawodnika.
     * @return wartość skillLevel
     */
    public int getSkillLevel() { return skillLevel; }

    /**
     * Pobiera wiek zawodnika w latach.
     * @return wiek zawodnika
     */
    public int getAge()        { return age; }

    // --- Zarządzanie punktacją w rankingu ATP ---

    /**
     * Pobiera aktualny wynik punktowy w rankingu ATP.
     * @return punkty rankingowe zawodnika
     */
    public int getRankingPoints() {
        return rankingPoints;
    }

    /**
     * Ustawia liczbę punktów rankingowych ATP zawodnika.
     * @param rankingPoints nowy stan punktów rankingowych
     */
    public void setRankingPoints(int rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    // --- Settery konfiguracyjne wykorzystywane przez kreator postaci w ConfigController ---

    /**
     * Ustawia wiek zawodnika.
     * @param age nowy wiek zawodnika
     */
    public void setAge(int age) { this.age = age; }

    /**
     * Ustawia preferowany typ kortu zawodnika.
     * @param favoriteCourt ulubiona nawierzchnia
     */
    public void setFavoriteCourt(CourtType favoriteCourt) { this.favoriteCourt = favoriteCourt; }

    /**
     * Ustawia siłę forehandu zawodnika.
     * @param forehandStrength nowy poziom forehandStrength
     */
    public void setForehandStrength(double forehandStrength) { this.forehandStrength = forehandStrength; }

    /**
     * Ustawia siłę backhandu zawodnika.
     * @param backhandStrength nowy poziom backhandStrength
     */
    public void setBackhandStrength(double backhandStrength) { this.backhandStrength = backhandStrength; }

    /**
     * Ustawia siłę serwisu zawodnika.
     * @param serveStrength nowy poziom serveStrength
     */
    public void setServeStrength(double serveStrength) { this.serveStrength = serveStrength; }

    // Settery dla nawierzchni (używane przy dystrybucji bonusów startowych dla ulubionego kortu)
    /**
     * Ustawia adaptację do gry na mączce.
     * @param clayStrength nowy poziom clayStrength
     */
    public void setClayStrength(double clayStrength) { this.clayStrength = clayStrength; }

    /**
     * Ustawia adaptację do gry na trawie.
     * @param grassStrength nowy poziom grassStrength
     */
    public void setGrassStrength(double grassStrength) { this.grassStrength = grassStrength; }

    /**
     * Ustawia adaptację do gry na betonie.
     * @param hardStrength nowy poziom hardStrength
     */
    public void setHardStrength(double hardStrength) { this.hardStrength = hardStrength; }

    /**
     * Modyfikuje konto punktowe zawodnika o określoną wartość przyrostu (dodatnią lub ujemną)
     * Wywoływane automatycznie po rozstrzygnięciu pojedynków meczowych przez silnik symulacji (MatchEngine)
     *
     * @param points Liczba punktów do dodania (lub odjęcia, jeśli wartość jest ujemna)
     */
    public void addRankingPoints(int points) {
        this.rankingPoints += points;
    }
}