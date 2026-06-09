package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `CourtType` - sprawdzają nazwy i opisy typu kortu.
 */
public class CourtTypeTest {

    /**
     * Sprawdza, czy getDisplayName zwraca oczekiwane nazwy dla wszystkich typów kortu
     */
    @Test
    public void getDisplayName_returnsExpectedValue() {
        assertEquals("Mączka", CourtType.CLAY.getDisplayName());
        assertEquals("Trawa", CourtType.GRASS.getDisplayName());
        assertEquals("Twarda", CourtType.HARD.getDisplayName());
    }

    /**
     * Sprawdza, czy getDescription zwraca opis dla każdego typu kortu
     */
    @Test
    public void getDescription_returnsExpectedValue() {
        assertEquals("Premiuje zawodników z dużą wytrzymałością", CourtType.CLAY.getDescription());
        assertEquals("Drastycznie premiuje serwis", CourtType.GRASS.getDescription());
        assertEquals("Najbardziej zbalansowana nawierzchnia", CourtType.HARD.getDescription());
    }
}
