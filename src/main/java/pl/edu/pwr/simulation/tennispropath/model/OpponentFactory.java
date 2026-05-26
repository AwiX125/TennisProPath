package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class OpponentFactory {
    private final Random random = new Random();

    private final String[] firstNames = {"Novak", "Carlos", "Daniil", "Jannik", "Alexander", "Stefanos", "Taylor", "Casper", "Andrey", "Holger"};
    private final String[] lastNames = {"Djoker", "Alcaraz", "Medved", "Sinner", "Zverev", "Tsitsipas", "Fritz", "Ruud", "Rublev", "Rune"};

    // Generuje przeciwnika dopasowanego do kategorii rankingowej gracza

    public Opponent createOpponentForCategory(RankingCategory category, int playerSkillLevel) {
        String randomName = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];

        int baseSkillMin;
        int baseSkillMax;
        switch (category) {
            case SILVER -> {
                baseSkillMin = 30;
                baseSkillMax = 55;
            }
            case GOLD -> {
                baseSkillMin = 50;
                baseSkillMax = 75;
            }
            case PLATINUM -> {
                baseSkillMin = 70;
                baseSkillMax = 95;
            }
            default -> {
                baseSkillMin = 10;
                baseSkillMax = 35;
            }
        }

        int opponentSkill = random.nextInt(baseSkillMax - baseSkillMin + 1) + baseSkillMin;
        int skillOffset = opponentSkill - playerSkillLevel;
        int botSkill = Math.max(10, playerSkillLevel + skillOffset);

        int randomAge = 18 + random.nextInt(28);

        double randomForehandStrength = 30 + random.nextInt(20);
        double randomBackhandStrength = 30 + random.nextInt(20);
        double randomServeStrength = 35 + random.nextInt(25);

        double randomClayStrength = 20 + random.nextInt(40);
        double randomGrassStrength = 20 + random.nextInt(40);
        double randomHardStrength = 20 + random.nextInt(40);

        return new Opponent(
            randomName,
            botSkill,
            randomAge,
            randomForehandStrength,
            randomBackhandStrength,
            randomServeStrength,
            randomGrassStrength,
            randomClayStrength,
            randomHardStrength
        );
    }
}