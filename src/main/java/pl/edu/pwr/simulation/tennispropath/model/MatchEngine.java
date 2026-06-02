package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

/**
 * Silnik meczowy odpowiedzialny za symulację pojedynków tenisowych
 * pomiędzy żywym graczem (Player) a przeciwnikiem komputerowym (Opponent).
 */
public class MatchEngine {
    private final Random random = new Random();

    /**
     * Główna metoda symulująca przebieg pojedynku tenisowego.
     * Oblicza finalną siłę uderzeniową obu zawodników na podstawie statystyk,
     * energii, nawierzchni oraz losowej formy dnia, a następnie rozdziela punkty ATP.
     *
     * @param player   Obiekt żywego gracza
     * @param opponent Obiekt wylosowanego z rosteru bota
     * @return Tekstowy log z podsumowaniem wyniku i punktacji dla konsoli/GUI
     */
    public String simulateMatch(Player player, Opponent opponent) {
        // 1. Losowanie nawierzchni, na której odbędzie się spotkanie
        CourtType[] types = CourtType.values();
        CourtType court = types[random.nextInt(types.length)];

        // Punkty pomocnicze do wyznaczenia asymetrii (kto ma przewagę w surowych statystykach)
        int playerWinChance = 1;
        int opponentWinChance = 1;

        // 2. Obliczanie wpływu zmęczenia na grę (Współczynnik Energii)
        // Zabezpieczenie przed dzieleniem przez zero w przypadku braku staminy
        double playerStaminaMax = player.getStamina() > 0 ? player.getStamina() : 100.0;
        double energyModifier = player.getEnergy() / playerStaminaMax; // Procentowy stan energii gracza

        // Obliczanie średniej bazy czystych umiejętności tenisowych (Forehand + Backhand + Serwis)
        double playerBaseTennisSkill = (player.getForehandStrength() + player.getBackhandStrength() + player.getServeStrength()) / 3.0;
        double opponentBaseTennisSkill = (opponent.getForehandStrength() + opponent.getBackhandStrength() + opponent.getServeStrength()) / 3.0;

        // 3. Naliczanie bonusu za nawierzchnię (20% wartości atrybutu danej nawierzchni dopisywane do siły)
        double playerCourtModifier = player.getStrengthForCourt(court) * 0.2;
        double opponentCourtModifier = opponent.getStrengthForCourt(court) * 0.2;

        // 4. Bezpośrednie porównanie poszczególnych uderzeń (Micro-matchups)
        playerWinChance += (player.getForehandStrength() > opponent.getForehandStrength()) ? 1 : 0;
        opponentWinChance += (player.getForehandStrength() <= opponent.getForehandStrength()) ? 1 : 0;

        playerWinChance += (player.getBackhandStrength() > opponent.getBackhandStrength()) ? 1 : 0;
        opponentWinChance += (player.getBackhandStrength() <= opponent.getBackhandStrength()) ? 1 : 0;

        playerWinChance += (player.getServeStrength() > opponent.getServeStrength()) ? 1 : 0;
        opponentWinChance += (player.getServeStrength() <= opponent.getServeStrength()) ? 1 : 0;


        // 5. Losowanie formy dnia dla obu zawodników (czynnik losowy od 0 do 24)
        int playerForm = random.nextInt(0,25);
        int opponentForm = random.nextInt(0, 25);

        // Wyznaczanie mnożnika przewagi psychicznej/technicznej na bazie micro-matchups
        double playerIndividualModifier = 1.0;
        double opponentIndividualModifier = 1.0;
        if (playerWinChance > opponentWinChance)
            playerIndividualModifier = 1.1; // +10% do finalnej siły, jeśli gracz wygrał większość porównań uderzeń
        else
            opponentIndividualModifier = 1.1; // +10% do finalnej siły dla bota w przeciwnym wypadku

        // Losowy mnożnik energii dla bota (boty zawsze mają stabilną energię w przedziale [0.8, 1.0])
        double opponentEnergyModifier = 0.8 + (random.nextDouble() * 0.2);

        // 6. Ostateczne równanie siły (Total Power) decydujące o wyniku seta/meczu
        double playerTotalPower = (playerBaseTennisSkill + playerCourtModifier + playerForm) * energyModifier * playerIndividualModifier;
        double opponentTotalPower = (opponentBaseTennisSkill + opponentCourtModifier + opponentForm) * opponentEnergyModifier * opponentIndividualModifier;

        // Rozegranie meczu kosztuje żywego gracza 30 punktów energii
        player.setEnergy(player.getEnergy() - 30);

        String logResult;

        // 7. Rozstrzygnięcie pojedynku i zapisanie statystyk w obiekcie Player
        boolean matchWon = playerTotalPower >= opponentTotalPower;
        player.recordMatchResult(matchWon);


        // ====================================================================
        // DYNAMICZNY SYSTEM PUNKTACJI RANKINGOWEJ (ZBLIŻONY DO ELO)
        // ====================================================================
        int skillDifference = player.getSkillLevel() - opponent.getSkillLevel(); // Różnica ogólnego poziomu

        int basePoints = 120;             // Bazowa stawka punktowa za wygrany/przegrany mecz
        int dynamicModifier = skillDifference * 4; // Skalowanie nagrody/kary (4 pkt rankingu za każdy 1 poziom skilla różnicy)

        // LOGIKA W PRZYPADKU WYGRANEJ GRACZA
        if (matchWon) {
            // Jeśli pokonałeś silniejszego, dostajesz więcej. Minimalny gwarantowany zysk to 25 punktów.
            int pointsForPlayer = Math.max(25, basePoints - dynamicModifier);
            // Bot traci połowę tego, co zyskał gracz (minimum 10 punktów)
            int pointsLostByOpponent = Math.max(10, pointsForPlayer / 2);

            player.addRankingPoints(pointsForPlayer);

            // Bezpiecznik zapobiegający spadkowi punktów bota poniżej zera
            if (opponent.getRankingPoints() - pointsLostByOpponent < 0) {
                opponent.setRankingPoints(0);
            } else {
                opponent.addRankingPoints(-pointsLostByOpponent);
            }

            // Sformatowanie komunikatu o sukcesie
            logResult = String.format("🏆 WYGRAŁ na nawierzchni %s z %s (+%d pkt ATP, rywal: -%d pkt)",
                    court.getDisplayName(), opponent.getName(), pointsForPlayer, pointsLostByOpponent);

            // LOGIKA W PRZYPADKU PRZEGRANEJ GRACZA
        } else {
            // Jeśli przegrałeś ze słabszym, tracisz drastycznie dużo. Minimalna strata to 15 punktów.
            int pointsLostByPlayer = Math.max(15, basePoints + dynamicModifier);
            // Bot otrzymuje nagrodę za zwycięstwo skalowaną o różnicę poziomów (minimum 25 punktów)
            int pointsForOpponent = Math.max(25, basePoints + (skillDifference * 2));

            // Bezpiecznik zapobiegający spadkowi punktów gracza poniżej zera
            if (player.getRankingPoints() - pointsLostByPlayer < 0) {
                player.setRankingPoints(0);
            } else {
                player.addRankingPoints(-pointsLostByPlayer);
            }

            opponent.addRankingPoints(pointsForOpponent);

            // Sformatowanie komunikatu o porażce
            logResult = String.format("❌ PRZEGRAŁ na nawierzchni %s z %s (-%d pkt ATP, rywal: +%d pkt)",
                    court.getDisplayName(), opponent.getName(), pointsLostByPlayer, pointsForOpponent);
        }

        return logResult; // Zwrócenie wygenerowanego wpisu kroniki meczowej
    }
}