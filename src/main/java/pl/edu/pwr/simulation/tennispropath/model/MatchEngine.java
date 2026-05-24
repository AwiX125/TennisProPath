package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class MatchEngine {
    private final Random random = new Random();

    public String simulateMatch(Player player, Opponent opponent) {
        CourtType[] types = CourtType.values();
        CourtType court = types[random.nextInt(types.length)];

        int playerWinChance = 1;
        int opponentWinChance = 1;

        double playerStaminaMax = player.getStamina() > 0 ? player.getStamina() : 100.0;
        double energyModifier = player.getEnergy() / playerStaminaMax;
        double playerBaseTennisSkill = (player.getForehandStrength() + player.getBackhandStrength() + player.getServeStrength()) / 3.0;
        double opponentBaseTennisSkill = (opponent.getForehandStrength() + opponent.getBackhandStrength() + opponent.getServeStrength()) / 3.0;

        double playerCourtModifier = 0;
        double opponentCourtModifier = 0;

        if (court == CourtType.GRASS) {
            playerCourtModifier = player.getGrassStrength() * 0.2;
            opponentCourtModifier = opponent.getGrassStrength() * 0.2;
        } else if (court == CourtType.CLAY) {
            playerCourtModifier = player.getClayStrength() * 0.2;
            opponentCourtModifier = opponent.getClayStrength() * 0.2;
        } else {
            playerCourtModifier = player.getHardStrength() * 0.2;
            opponentCourtModifier = opponent.getHardStrength() * 0.2;
        }

        if (player.getForehandStrength() > opponent.getForehandStrength()) playerWinChance++;
        else opponentWinChance++;

        if (player.getBackhandStrength() > opponent.getBackhandStrength()) playerWinChance++;
        else opponentWinChance++;

        if (player.getServeStrength() > opponent.getServeStrength()) playerWinChance++;
        else opponentWinChance++;


        int playerForm = random.nextInt(0,10);
        int opponentForm = random.nextInt(0, 10);

        double playerIndividualModifier = 1.0;
        double opponentIndividualModifier = 1.0;
        if (playerWinChance > opponentWinChance)
            playerIndividualModifier = 1.25;
        else
            opponentIndividualModifier = 1.25;

        double opponentEnergyModifier = 0.8 + (random.nextDouble() * 0.2);

        double playerTotalPower = (playerBaseTennisSkill + playerCourtModifier + playerForm) * energyModifier * playerIndividualModifier;
        double opponentTotalPower = (opponentBaseTennisSkill + opponentCourtModifier + opponentForm) * opponentEnergyModifier * opponentIndividualModifier;

        player.setEnergy(player.getEnergy() - 30);

        String logResult;

        boolean playerMatchResult = playerTotalPower >= opponentTotalPower;
        player.recordMatchResult(playerMatchResult);

        if (playerMatchResult) {
            player.addRankingPoints(250);
            logResult = "🏆 WYGRAŁ na nawierzchni " + court.getDisplayName() + " z " + opponent.getName() + " (+250 pkt)";
        } else {
            player.addRankingPoints(20);
            logResult = "❌ PRZEGRAŁ na nawierzchni " + court.getDisplayName() + " z " + opponent.getName() + " (+20 pkt)";
        }

        return logResult;
    }
}