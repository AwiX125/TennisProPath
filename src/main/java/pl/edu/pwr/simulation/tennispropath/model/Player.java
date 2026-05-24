package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class Player extends TennisPlayer {

    // Profil gracza
    private CourtType favoriteCourt;
    private double energy;
    private double stamina;
    private int experience;

    // Tymczasowe
    private boolean isInjured;
    private int xpForCurrentLevel = 0;
    private int xpRequiredForNextLevel = 100;
    boolean leveledUp = false;

    // Ranking
    private int rankingPoints;

    // Statystyki meczu
    private int matchesPlayed = 0;
    private int matchesWon = 0;

    /**
    * Zapisuje wynik meczu
    * */
    public void recordMatchResult(boolean won) {
        this.matchesPlayed++;
        if (won) {
            this.matchesWon++;
        }
    }



    private final Random random = new Random();


    public Player(String name) {
        super(name, 20, 18);

        this.energy = 100;
        this.experience = 0;
        this.rankingPoints = 0;
        this.isInjured = false;

        this.stamina = 100;

        // Losowanie profilu zawodnika na start kariery
        this.forehandStrength = 30 + random.nextInt(20);
        this.backhandStrength = 30 + random.nextInt(20);
        this.serveStrength = 35 + random.nextInt(25);

        // Losowanie predyspozycji do nawierzchni
        this.clayStrength = 20 + random.nextInt(40);  // 20 - 60
        this.grassStrength = 20 + random.nextInt(40);
        this.hardStrength = 20 + random.nextInt(40);

        determineFavoriteCourt();
    }

    /**
     * Określa ulubiony kort zawodnika na podstawie predyspozycji do nawierzchni
     */
    private void determineFavoriteCourt() {
        if (clayStrength >= grassStrength && clayStrength >= hardStrength) {
            this.favoriteCourt = CourtType.CLAY;
        } else if (grassStrength >= clayStrength && grassStrength >= hardStrength) {
            this.favoriteCourt = CourtType.GRASS;
        } else {
            this.favoriteCourt = CourtType.HARD;
        }
    }

    /**
     * Symuluje trening zawodnika, podnosi umiejętności i doświadczenie, kosztuje energię
     */
    public void train() {
        // Gracz nie może trenować będąc kontuzjowanym
        if (isInjured) return;

        this.energy -= 5 + random.nextInt(5, 21);

        int gainedXp =  random.nextInt(5, 31);
        this.experience += gainedXp;
        this.xpForCurrentLevel += gainedXp;

        if (this.xpForCurrentLevel > this.xpRequiredForNextLevel) {
            this.skillLevel++;

            this.xpForCurrentLevel -= this.xpRequiredForNextLevel;

            this.xpRequiredForNextLevel = (int)(this.xpRequiredForNextLevel * 1.2) + 10;

            leveledUp = true;
            System.out.println("AWANS! " + name + " wskoczył na poziom " + this.skillLevel +
                    ". Kolejny poziom wymaga: " + this.xpRequiredForNextLevel + " XP.");
        }

        int roll = random.nextInt(3);
        if (roll == 0 && forehandStrength < 100) forehandStrength += random.nextDouble(0.3, 1);
        if (roll == 1 && backhandStrength < 100) backhandStrength += random.nextDouble(0.3, 1);
        if (roll == 2 && serveStrength < 100) serveStrength += random.nextDouble(0.3, 1);

        this.stamina += 0.5;

        checkStaminaBounds();
    }

    /**
     * Regeneruje siły zawodnika
     */
    public void rest() {
        // lat > 50 => 0.6
        // lat > 30 => 0.8
        // lat < 30 => 1
        double ageFactor = (age > 30) ? (age > 50) ? 0.6 : 0.8 : 1.0;
        int recovery = (int) (25 * stamina * 0.01 * ageFactor);

        this.energy += recovery;

        if (isInjured) {
            if (random.nextInt(100) < 30 * stamina * 0.01) {
                this.isInjured = false;
                System.out.println(name + " wyleczył kontuzję i wraca do gry!");
            }
        }
        checkStaminaBounds();
    }

    public void celebrateBirthday() {
        this.age++;

        if (this.age > 30) {
            this.stamina -= 5;
        }

        if (this.age > 50) {
            this.stamina -= 10;
        }
    }

    /**
     * Autonomiczna decyzja zawodnika na dany tydzień symulacji
     * Zawodnik sam analizuje swój stan i wybiera działanie
     */
    public void act() {
        if (isInjured) {
            rest();
        } else if (energy < (0.66 * stamina)) {
            rest();
        } else {
            train();
        }
    }

    /**
     * Metoda pomocnicza pilnująca, aby stamina nie wyszła poza 0-100
     * oraz sprawdzająca ryzyko kontuzji przy skrajnym zmęczeniu
     */
    private void checkStaminaBounds() {
        if (this.energy > stamina) this.energy = this.stamina;
        if (this.energy < 0) this.energy = 0;

        if (this.energy < 20 && !isInjured) {
            if (random.nextInt(100) < 30) {
                this.isInjured = true;
                System.out.println(name + " doznał kontuzji z powodu przemęczenia!");
            }
        }
    }

    // GETTERY I SETTERY

    // profil
    public double getEnergy() { return energy; }
    public void setEnergy(double energy) { this.energy = energy; checkStaminaBounds(); }
    public int getExperience() { return experience; }
    public double getStamina() { return stamina; }
    public CourtType getFavoriteCourt() { return favoriteCourt; }

    // ranking
    public int getRankingPoints() { return rankingPoints; }
    public void addRankingPoints(int points) { this.rankingPoints += points; }

    // tymczasowe
    public boolean isInjured() { return isInjured; }
    public boolean isLeveledUp() { return leveledUp; }
    public void setLeveledUp(boolean leveledUp) { this.leveledUp = leveledUp; }
    public int getXpRequiredForNextLevel() {return this.xpRequiredForNextLevel;}
    public int getXpForCurrentLevel() {return this.xpForCurrentLevel;}

    // statystyki meczu
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getMatchesWon() { return matchesWon; }
    public int getMatchesLost() { return matchesPlayed - matchesWon; }
    public double getWinRate() {
        if (matchesPlayed == 0) return 0.0;
        return (double) matchesWon / matchesPlayed * 100.0;
    }
}
