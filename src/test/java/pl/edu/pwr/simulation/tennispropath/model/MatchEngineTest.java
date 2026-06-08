package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `MatchEngine` - sprawdzają redukcję energii i zapis wyniku.
 */
public class MatchEngineTest {
    private static final double EPS = 0.000001;

    /**
     * Sprawdza, czy symulacja meczu obniża energię gracza o 30, zapisuje rozegrany mecz oraz zwraca  wynik.
     */
    @Test
    public void simulateMatch_reducesPlayerEnergy_and_recordsMatch() {
        Player p = new Player("Tester");
        p.setEnergy(100.0);

        Opponent o = new Opponent("Bot", 10, 25,
                10.0, 10.0, 10.0,
                10.0, 10.0, 10.0);

        MatchEngine engine = new MatchEngine();
        String result = engine.simulateMatch(p, o);

        assertEquals(70.0, p.getEnergy(), EPS);
        assertTrue(p.getMatchesPlayed() >= 1);
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}
