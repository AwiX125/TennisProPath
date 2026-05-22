package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class Player extends TennisPlayer {

    private int stamina;
    private int experience;
    private int rankingPoints;
    private boolean isInjured;

    private int matchesPlayed = 0;
    private int matchesWon = 0;

    public void recordMatchResult(boolean won) {
        this.matchesPlayed++;
        if (won) {
            this.matchesWon++;
        }
    }

    public double getWinRate() {
        if (matchesPlayed == 0) return 0.0;
        return (double) matchesWon / matchesPlayed * 100.0;
    }

    private final Random random = new Random();


    public Player(String name) {
        super(name, 15);

        this.stamina = 100;
        this.experience = 0;
        this.rankingPoints = 0;
        this.isInjured = false;
    }

    /**
     * Symuluje trening zawodnika, podnosi umiejętności i doświadczenie, kosztuje energię
     */
    public void train() {
        if (isInjured) return;

        this.stamina -= 15;
        this.experience += 10;

        if (this.experience % 50 == 0) {
            this.skillLevel++;
        }

        checkStaminaBounds();
    }

    /**
     * Regeneruje siły zawodnika
     */
    public void rest() {
        this.stamina += 30;
        if (isInjured) {
            if (random.nextInt(100) < 40) {
                this.isInjured = false;
                System.out.println(name + " wyleczył kontuzję i wraca do gry!");
            }
        }
        checkStaminaBounds();
    }

    /**
     * Autonomiczna decyzja zawodnika na dany tydzień symulacji
     * Zawodnik sam analizuje swój stan i wybiera działanie
     */
    public void act() {
        if (isInjured) {
            rest();
        } else if (stamina < 30) {
            rest();
        } else {
            train();
        }
    }

    /**
     * Prywatna metoda pomocnicza pilnująca, aby stamina nie wyszła poza 0-100
     * oraz sprawdzająca ryzyko kontuzji przy skrajnym zmęczeniu
     */
    private void checkStaminaBounds() {
        if (this.stamina > 100) this.stamina = 100;
        if (this.stamina < 0) this.stamina = 0;

        if (this.stamina < 20 && !isInjured) {
            if (random.nextInt(100) < 30) {
                this.isInjured = true;
                System.out.println(name + " doznał kontuzji z powodu przemęczenia!");
            }
        }
    }


    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; checkStaminaBounds(); }

    public int getExperience() { return experience; }

    public int getRankingPoints() { return rankingPoints; }
    public void addRankingPoints(int points) { this.rankingPoints += points; }

    public boolean isInjured() { return isInjured; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public int getMatchesWon() { return matchesWon; }
    public int getMatchesLost() { return matchesPlayed - matchesWon; }
}
