package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class InjurySystem {
    private final Random random = new Random();

    //Wykonuje losowanie kontuzji na podstawie aktualnego zmęczenia.

    public boolean rollForInjury(double fatigue) {
        double normalizedFatigue = Math.max(0.0, Math.min(1.0, fatigue));

        int baseChance;
        if (normalizedFatigue < 0.3) {
            baseChance = 2;
        } else if (normalizedFatigue < 0.5) {
            baseChance = 8;
        } else if (normalizedFatigue < 0.7) {
            baseChance = 15;
        } else if (normalizedFatigue < 0.9) {
            baseChance = 30;
        } else {
            baseChance = 45;
        }

        int volatility = random.nextInt(21); // dodatkowa losowość 0-20
        int chancePercent = Math.min(100, baseChance + volatility);

        return random.nextInt(100) < chancePercent;
    }


    // Oblicza poziom zmęczenia na podstawie energii i wytrzymałości.

    public double calculateFatigue(double energy, double stamina) {
        if (stamina <= 0.0) {
            return 1.0;
        }

        double fatigue = 1.0 - (energy / stamina);
        return Math.max(0.0, Math.min(1.0, fatigue));
    }

    // Określa czas powrotu do zdrowia dla kontuzji na podstawie poziomu zmęczenia.

    public int determineRecoveryDuration(double fatigue) {
        double normalizedFatigue = Math.max(0.0, Math.min(1.0, fatigue));
        int baseWeeks;

        if (normalizedFatigue < 0.3) {
            baseWeeks = 1;
        } else if (normalizedFatigue < 0.5) {
            baseWeeks = 2;
        } else if (normalizedFatigue < 0.7) {
            baseWeeks = 3;
        } else if (normalizedFatigue < 0.9) {
            baseWeeks = 4;
        } else {
            baseWeeks = 5;
        }

        return baseWeeks + random.nextInt(2);
    }
}
