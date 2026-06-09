package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `Opponent` - konstruktor i zachowanie odziedziczonych statystyk.
 */
public class OpponentTest {

    /**
     * Sprawdza, czy konstruktor Opponent ustawia poprawne dane zawodnika.
     */
    @Test
    public void constructor_setsOpponentFields() {
        Opponent o = new Opponent("Bot", 42, 25, 10.5, 11.5, 12.5, 20.0, 21.0, 22.0);

        assertEquals("Bot", o.getName());
        assertEquals(42, o.getSkillLevel());
        assertEquals(25, o.getAge());
        assertEquals(10.5, o.getForehandStrength());
        assertEquals(11.5, o.getBackhandStrength());
        assertEquals(12.5, o.getServeStrength());
        assertEquals(21.0, o.getClayStrength());
        assertEquals(20.0, o.getGrassStrength());
        assertEquals(22.0, o.getHardStrength());
    }
}
