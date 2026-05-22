package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

public class MatchEngine {
    private final Random random = new Random();

    public String simulateMatch(Player player, Opponent opponent) {
        CourtType[] types = CourtType.values();
        CourtType court = types[random.nextInt(types.length)];

        double staminaModifier = player.getStamina() / 100.0;
        double effectivePlayerSkill = player.getSkillLevel() * staminaModifier;

        double courtModifier = 0;
        if (court == CourtType.GRASS) {
            courtModifier = 4.0;
        } else if (court == CourtType.CLAY) {
            courtModifier = -3.0;
        }

        int playerForm = random.nextInt(21) - 10;
        int opponentForm = random.nextInt(21) - 10;

        double playerTotalPower = effectivePlayerSkill + courtModifier + playerForm;
        double opponentTotalPower = opponent.getSkillLevel() + opponentForm;

        player.setStamina(player.getStamina() - 30);

        String logResult;

        if (playerTotalPower >= opponentTotalPower) {
            player.addRankingPoints(250);
            player.recordMatchResult(true);
            logResult = "🏆 WYGRAŁ na nawierzchni " + court.getDisplayName() + " z " + opponent.getName() + " (+250 pkt)";
        } else {
            player.addRankingPoints(20);
            player.recordMatchResult(false);
            logResult = "❌ PRZEGRAŁ na nawierzchni " + court.getDisplayName() + " z " + opponent.getName() + " (+20 pkt)";
        }

        return logResult;
    }
}