package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

/**
 * Fabryka odpowiedzialna za losowe generowanie obiektów przeciwników (Opponent)
 * Tworzy zawodników komputerowych o zróżnicowanych personaliach, kategoriach rankingowych,
 * umiejętnościach technicznych dostosowanych do ich klasy oraz punktach startowych ATP
 */
public class OpponentFactory {
    // Wspólny generator liczb pseudolosowych dla całej fabryki
    private final Random random = new Random();

    // Baza imion inspirowana czołówką współczesnego męskiego tenisa
    private final String[] firstNames = {"Novak", "Carlos", "Daniil", "Jannik", "Alexander", "Stefanos", "Taylor", "Casper", "Andrey", "Holger"};
    // Baza nazwisk/pseudonimów dopasowana do imion
    private final String[] lastNames = {"Djoker", "Alcaraz", "Medved", "Sinner", "Zverev", "Tsitsipas", "Fritz", "Ruud", "Rublev", "Rune"};

    /**
     * Generuje unikalnego zawodnika do globalnej puli ATP World Tour
     * Każdy bot dostaje losową kategorię potencjału (od BRONZE do PLATINUM),
     * dzięki czemu w tabeli uzyskujemy rozkład sił
     * @return Nowo utworzony i sparametryzowany obiekt klasy Opponent
     */
    public Opponent createGlobalTourPlayer() {
        // Losowanie tożsamości zawodnika z dostępnych tablic tekstowych
        String randomName = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];

        // Losowanie wieku bota w przedziale od 18 do 45 lat
        int botAge = 18 + random.nextInt(28);

        // Losowanie "klasy" lub "tieru" zawodnika
        RankingCategory[] categories = RankingCategory.values();
        RankingCategory randomCategory = categories[random.nextInt(categories.length)];

        // Zmienne pomocnicze definiujące minimalne i maksymalne ramy ogólnego skilla dla danej kategorii
        int baseSkillMin;
        int baseSkillMax;

        // Instrukcja warunkowa przypisująca widełki umiejętności na podstawie wylosowanego tieru
        switch (randomCategory) {
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
            default -> { // Domyślnie kategoria BRONZE - zawodnicy początkujący / zamykający stawkę
                baseSkillMin = 10;
                baseSkillMax = 35;
            }
        }

        // Wyznaczenie ostatecznej wartości Skill Level w ramach wylosowanych widełek kategorii
        int botSkill = random.nextInt(baseSkillMax - baseSkillMin + 1) + baseSkillMin;

        // Obliczanie mnożnika umiejętności
        // Służy do skalowania statystyk uderzeń bota, aby silniejszy ogólnie zawodnik miał też silniejsze zagrania
        double skillMultiplier = 0.5 + (botSkill / 100.0);

        // Losowanie bazowej siły uderzeń i serwisu, a następnie przemnożenie ich przez wyliczony wyżej współczynnik
        double fHand = (20.0 + random.nextInt(60)) * skillMultiplier; // Forehand bazowy w zakresie [20, 79] * multiplier
        double bHand = (20.0 + random.nextInt(60)) * skillMultiplier; // Backhand bazowy w zakresie [20, 79] * multiplier
        double serve = (25.0 + random.nextInt(65)) * skillMultiplier; // Serwis bazowy w zakresie [25, 89] * multiplier

        // Losowanie niezależnych współczynników adaptacji do konkretnych nawierzchni (od 20 do 89 punktów)
        double clay = 20.0 + random.nextInt(70);  // Przystosowanie do kortów ziemnych
        double grass = 20.0 + random.nextInt(70); // Przystosowanie do kortów trawiastych
        double hard = 20.0 + random.nextInt(70);  // Przystosowanie do kortów twardych

        // 6. Wywołanie konstruktora i powołanie do życia kompletnego obiektu przeciwnika
        Opponent opponent = new Opponent(
                randomName,
                botSkill,
                botAge,
                fHand,
                bHand,
                serve,
                clay,
                grass,
                hard
        );

        // Generowanie startowego dorobku punktowego w rankingu ATP
        // Punkty są przyznawane proporcjonalnie do umiejętności bota (skil * 5), z lekkim losowym odchyleniem do 49 pkt
        int startPoints = botSkill * 5 + random.nextInt(50);
        opponent.setRankingPoints(startPoints);

        return opponent; // Zwrócenie w pełni uformowanego zawodnika
    }
}