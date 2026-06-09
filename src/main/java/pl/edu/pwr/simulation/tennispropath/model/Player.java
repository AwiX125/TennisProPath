package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

/**
 * Klasa reprezentująca gracza w symulacji.
 * Dziedziczy po TennisPlayer i obsługuje energię, trening, regenerację, kontuzje,
 * statystyki meczowe oraz progresję doświadczenia.
 */
public class Player extends TennisPlayer {

    // Profil gracza - podstawowe atrybuty fizyczne i rozwojowe
    private double energy;           // Aktualny poziom energii (sił) zawodnika
    private double stamina;          // Maksymalny pułap kondycji (wydolności)
    private int experience;          // Całkowite zgromadzone doświadczenie (XP)

    // Tymczasowe - flagi stanu zdrowia i mechaniki awansów poziomu (Level Up)
    private boolean isInjured;              // Flaga określająca, czy gracz ma kontuzję
    private int injuryWeeksRemaining = 0;   // Liczba tygodni pozostałych do pełnego wyleczenia
    private int xpForCurrentLevel = 0;      // Liczba punktów XP zdobytych na obecnym poziomie
    private int xpRequiredForNextLevel = 100;// Próg XP wymagany do osiągnięcia kolejnego poziomu
    boolean leveledUp = false;              // Flaga informująca interfejs o zaistniałym awansie

    // Statystyki meczu - śledzenie bilansu kariery gracza
    private int matchesPlayed = 0; // Łączna liczba rozegranych pojedynków
    private int matchesWon = 0;    // Liczba wygranych spotkań

    /**
     * Zapisuje wynik meczu
     * Zwiększa licznik rozegranych gier oraz, w przypadku sukcesu, licznik zwycięstw
     * @param won flaga określająca, czy mecz został wygrany
     */
    public void recordMatchResult(boolean won) {
        this.matchesPlayed++;
        if (won) {
            this.matchesWon++;
        }
    }



    // Systemy pomocnicze - generowanie losowości oraz zewnętrzny kalkulator kontuzji
    private final Random random = new Random();
    private final InjurySystem injurySystem = new InjurySystem();


    /**
     * Konstruktor tworzący nowego gracza o zadanym imieniu
     * Inicjalizuje wiek (20 lat), startowy poziom skilla (18) oraz losuje parametry początkowe
     * @param name imię zawodnika
     */
    public Player(String name) {
        super(name, 20, 18); // Wywołanie konstruktora klasy bazowej TennisPlayer(name, age, skillLevel)

        this.energy = 100;      // Start z pełnym paskiem energii
        this.experience = 0;    // Brak punktów doświadczenia na start
        this.isInjured = false; // Gracz rozpoczyna karierę w pełni zdrowia

        this.stamina = 100;     // Maksymalna startowa wydolność organizmu
    }

    /**
     * Symuluje trening zawodnika, podnosi umiejętności i doświadczenie, kosztuje energię
     */
    public void train() {
        // Gracz nie może trenować będąc kontuzjowanym - przerwanie metody
        if (isInjured) return;

        // Trening zużywa losową ilość energii z przedziału od 5 do 25 punktów
        this.energy -= 5 + random.nextInt(5, 21);

        // Losowanie i dodawanie zdobytych punktów doświadczenia [5, 35]
        int gainedXp =  random.nextInt(5, 31);
        this.experience += gainedXp;
        this.xpForCurrentLevel += gainedXp;

        // Algorytm obsługi awansu na wyższy poziom (Level Up)
        if (this.xpForCurrentLevel > this.xpRequiredForNextLevel) {
            this.skillLevel++; // Zwiększenie ogólnego poziomu umiejętności (skilla)

            // Przeniesienie nadmiarowego doświadczenia do nowego poziomu
            this.xpForCurrentLevel -= this.xpRequiredForNextLevel;

            // Skalowanie progu trudności kolejnego poziomu (+20% oraz stały bonus 10 XP)
            this.xpRequiredForNextLevel = (int)(this.xpRequiredForNextLevel * 1.2) + 10;

            leveledUp = true; // Flaga dla kontrolera GUI
            System.out.println("AWANS! " + name + " wskoczył na poziom " + this.skillLevel +
                    ". Kolejny poziom wymaga: " + this.xpRequiredForNextLevel + " XP.");
        }

        // Losowy rozwój jednej z trzech głównych cech tenisowych (maksymalny limit to 100)
        // Losowanie indeksu cechy: 0 = Forehand, 1 = Backhand, 2 = Serwis
        int roll = random.nextInt(3);
        double skillGain = random.nextDouble(0.1, 0.5);

        switch (roll) {
            case 0 -> this.forehandStrength = Math.min(100.0, this.forehandStrength + skillGain);
            case 1 -> this.backhandStrength = Math.min(100.0, this.backhandStrength + skillGain);
            case 2 -> this.serveStrength    = Math.min(100.0, this.serveStrength + skillGain);
        }

        // Losowy rozwój jednej z trzech głównych nawierzchni tenisowych (maksymalny limit to 100)
        // Losowanie indeksu nawierzchni: 0 = Trawa, 1 = Mączka, 2 = Beton
        int rollCourt = random.nextInt(3);
        double courtGain = random.nextDouble(0.1, 0.5);

        switch (rollCourt) {
            case 0 -> this.grassStrength = Math.min(100.0, this.grassStrength + courtGain);
            case 1 -> this.clayStrength  = Math.min(100.0, this.clayStrength + courtGain);
            case 2 -> this.hardStrength  = Math.min(100.0, this.hardStrength + courtGain);
        }

        this.stamina += 0.5;

        checkStaminaBounds(); // Walidacja i ewentualne sprawdzenie ryzyka kontuzji
    }

    /**
     * Regeneruje siły zawodnika
     */
    public void rest() {
        // Wyznaczenie współczynnika starości organizmu - im starszy zawodnik, tym wolniej się regeneruje
        // lat > 50 => 0.6
        // lat > 30 => 0.8
        // lat < 30 => 1
        double ageFactor = (age > 30) ? (age > 50) ? 0.6 : 0.8 : 1.0;

        // Obliczenie odzyskiwanej energii na podstawie aktualnego pułapu stamina oraz wieku
        int recovery = (int) (25 * stamina * 0.01 * ageFactor);

        this.energy += recovery;

        // Jeśli zawodnik odpoczywa w trakcie kontuzji, skraca czas rekonwalescencji o 1 tydzień
        if (isInjured) {
            injuryWeeksRemaining = Math.max(0, injuryWeeksRemaining - 1);
            if (injuryWeeksRemaining == 0) {
                this.isInjured = false; // Powrót do pełnego zdrowia
                System.out.println(name + " wyleczył kontuzję i wraca do gry!");
            }
        }
        checkStaminaBounds(); // Upewnienie się, że energia nie przekroczyła maksymalnej staminy
    }

    /**
     * Coroczna symulacja starzenia się zawodnika.
     * Po przekroczeniu określonych barier wiekowych (30 i 50 lat) drastycznie spada maksymalna stamina
     */
    public void celebrateBirthday() {
        this.age++;

        if (this.age > 50) {
            this.stamina -= 10;
        } else if (this.age > 30) {
            this.stamina -= 5;
        }

        if (this.stamina < 50) {
            this.stamina = 50;
        }
    }

    /**
     * Autonomiczna decyzja zawodnika na dany tydzień symulacji
     * Zawodnik sam analizuje swój stan i wybiera działanie
     */
    public void act() {
        if (isInjured) {
            rest(); // Kontuzjowany gracz bezwzględnie odpoczywa
        } else if (energy < (0.66 * stamina)) {
            rest(); // Gracz odpoczywa, jeśli jego energia spadła poniżej ~66% maksymalnej staminy
        } else {
            train(); // W każdym innym przypadku (gdy jest wypoczęty), podejmuje trening
        }
    }

    /**
     * Metoda pomocnicza pilnująca, aby stamina nie wyszła poza 0-100
     * oraz sprawdzająca ryzyko kontuzji przy skrajnym zmęczeniu
     */
    private void checkStaminaBounds() {
        // Ograniczenie poziomu energii do aktualnych widełek [0, stamina]
        if (this.energy > stamina) this.energy = this.stamina;
        if (this.energy < 0) this.energy = 0;

        // Jeśli energia spadnie poniżej niebezpiecznej bariery 30%, losujemy szansę na kontuzję
        if (this.energy < this.stamina * .3 && !isInjured) {
            if (injurySystem.rollForInjury(getFatigueLevel())) {
                this.isInjured = true;
                // Wyznaczenie czasu trwania rekonwalescencji w zależności od stopnia przemęczenia
                this.injuryWeeksRemaining = injurySystem.determineRecoveryDuration(getFatigueLevel());
                System.out.println(name + " doznał kontuzji z powodu przemęczenia! Rekonwalescencja: " + injuryWeeksRemaining + " tygodni.");
            }
        }
    }

    /**
     * Pobiera obliczony procentowy współczynnik zmęczenia organizmu
     * @return aktualny poziom zmęczenia jako wartość procentowa
     */
    public double getFatigueLevel() {
        return injurySystem.calculateFatigue(this.energy, this.stamina);
    }

    /**
     * Pobiera liczbę tygodni pozostałych do zakończenia leczenia kontuzji
     * @return liczba pozostałych tygodni rekonwalescencji
     */
    public int getInjuryWeeksRemaining() {
        return injuryWeeksRemaining;
    }

    // ==========================================
    // GETTERY I SETTERY
    // ==========================================

    // Gettery i settery profilu fizycznego i nawierzchni
    /**
     * Pobiera aktualną energię zawodnika.
     * @return aktualny poziom energii
     */
    public double getEnergy() { return energy; }

    /**
     * Ustawia poziom energii zawodnika i sprawdza limity względem staminy.
     * @param energy nowy poziom energii
     */
    public void setEnergy(double energy) { this.energy = energy; checkStaminaBounds(); }

    /**
     * Pobiera zgromadzone doświadczenie zawodnika w punktach XP.
     * @return suma doświadczenia zawodnika
     */
    public int getExperience() { return experience; }

    /**
     * Pobiera maksymalny pułap staminy zawodnika.
     * @return aktualny poziom staminy
     */
    public double getStamina() { return stamina; }

    // Gettery i settery stanów tymczasowych / flag symulatora
    /**
     * Sprawdza, czy zawodnik jest obecnie kontuzjowany.
     * @return true, jeśli zawodnik ma kontuzję
     */
    public boolean isInjured() { return isInjured; }

    /**
     * Sprawdza, czy zawodnik zdobył awans poziomu i flaga jest ustawiona.
     * @return true, jeśli zawodnik awansował
     */
    public boolean isLeveledUp() { return leveledUp; }

    /**
     * Ustawia flagę informującą o awansie zawodnika.
     * @param leveledUp flaga awansu
     */
    public void setLeveledUp(boolean leveledUp) { this.leveledUp = leveledUp; }

    /**
     * Pobiera wymagane XP do następnego poziomu.
     * @return liczba punktów XP potrzebnych do awansu
     */
    public int getXpRequiredForNextLevel() {return this.xpRequiredForNextLevel;}

    /**
     * Pobiera XP zdobyte na aktualnym poziomie zawodnika.
     * @return punkty XP zdobyte w bieżącym poziomie
     */
    public int getXpForCurrentLevel() {return this.xpForCurrentLevel;}

    // Gettery i kalkulatory statystyk meczowych
    /**
     * Pobiera liczbę rozegranych meczów przez zawodnika.
     * @return liczba rozegranych meczów
     */
    public int getMatchesPlayed() { return matchesPlayed; }

    /**
     * Pobiera liczbę wygranych meczów przez zawodnika.
     * @return liczba zwycięstw w meczach
     */
    public int getMatchesWon() { return matchesWon; }

    /**
     * Pobiera liczbę przegranych meczów jako różnicę rozegranych i wygranych.
     * @return liczba przegranych meczów
     */
    public int getMatchesLost() { return matchesPlayed - matchesWon; }

    /**
     * Oblicza procentowy współczynnik zwycięstw (Win Rate).
     * Zwraca 0.0, jeśli zawodnik nie rozegrał jeszcze żadnego spotkania.
     * @return procentowy wskaźnik wygranych meczów
     */
    public double getWinRate() {
        if (matchesPlayed == 0) return 0.0;
        return (double) matchesWon / matchesPlayed * 100.0;
    }
}