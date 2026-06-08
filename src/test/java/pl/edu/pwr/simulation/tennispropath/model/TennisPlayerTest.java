package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `TennisPlayer` - zachowanie statystyk tenisisty.
 */
public class TennisPlayerTest {

    private static class TestPlayer extends TennisPlayer {
        public TestPlayer() {
            super("Test", 10, 25);
            this.clayStrength = 11.0;
            this.grassStrength = 22.0;
            this.hardStrength = 33.0;
        }
    }

    @Test
    public void getStrengthForCourt_mapsToCorrectField() {
        TestPlayer t = new TestPlayer();
        assertEquals(11.0, t.getStrengthForCourt(CourtType.CLAY));
        assertEquals(22.0, t.getStrengthForCourt(CourtType.GRASS));
        assertEquals(33.0, t.getStrengthForCourt(CourtType.HARD));
    }

    /**
     * Sprawdza, czy addRankingPoints poprawnie aktualizuje punkty rankingowe.
     */
    @Test
    public void addRankingPoints_updatesRankingCorrectly() {
        TestPlayer t = new TestPlayer();

        assertEquals(0, t.getRankingPoints());
        t.addRankingPoints(10);
        assertEquals(10, t.getRankingPoints());

        t.addRankingPoints(-10);
        assertEquals(0, t.getRankingPoints());
    }
}
