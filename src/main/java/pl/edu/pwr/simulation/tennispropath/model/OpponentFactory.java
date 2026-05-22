package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class OpponentFactory {
    private final Random random = new Random();

    private final String[] firstNames = {"Novak", "Carlos", "Daniil", "Jannik", "Alexander", "Stefanos", "Taylor", "Casper", "Andrey", "Holger"};
    private final String[] lastNames = {"Djoker", "Alcaraz", "Medved", "Sinner", "Zverev", "Tsitsipas", "Fritz", "Ruud", "Rublev", "Rune"};

    /**
     * Generuje losowego przeciwnika
     * Poziom bota zależy od obecnego poziomu gracza
     */
    public Opponent createRandomOpponent(int playerSkillLevel) {
        String randomName = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];

        int skillOffset = random.nextInt(16) - 5;
        int botSkill = playerSkillLevel + skillOffset;

        if (botSkill < 10) botSkill = 10;

        return new Opponent(randomName, botSkill);
    }
}