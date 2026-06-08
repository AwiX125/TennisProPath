package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `OpponentFactory` - sprawdzają poprawność generowanych parametrów.
 */
public class OpponentFactoryTest {

    /**
     * Sprawdza, czy fabryka tworzy sensownego przeciwnika z poprawnym wiekiem,
     * poziomem umiejętności i punktami rankingowymi.
     */
    @Test
    public void createGlobalTourPlayer_generatesValidOpponent() {
        OpponentFactory factory = new OpponentFactory();

        for (int i = 0; i < 20; i++) {
            Opponent opponent = factory.createGlobalTourPlayer();

            assertNotNull(opponent);
            assertFalse(opponent.getName().isBlank());
            assertTrue(opponent.getAge() >= 18 && opponent.getAge() <= 45);
            assertTrue(opponent.getSkillLevel() >= 10 && opponent.getSkillLevel() <= 95);
            assertTrue(opponent.getRankingPoints() >= opponent.getSkillLevel() * 5);
            assertTrue(opponent.getRankingPoints() <= opponent.getSkillLevel() * 5 + 49);
            assertNotNull(RankingCategory.fromPoints(opponent.getRankingPoints()));
        }
    }
}
