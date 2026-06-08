package pl.edu.pwr.simulation.tennispropath.model;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `WorldTourRoster` - rejestracja gracza, progres i logika doboru przeciwników.
 */
public class WorldTourRosterTest {

    /**
     * Sprawdza, czy rejestracja żywego gracza dodaje go tylko raz do listy aktywnych graczy.
     */
    @Test
    public void registerHumanPlayer_addsHumanOnlyOnce() {
        WorldTourRoster roster = new WorldTourRoster();
        Player human = new Player("Human");

        int before = roster.getActivePlayers().size();
        roster.registerHumanPlayer(human);
        roster.registerHumanPlayer(human);

        ObservableList<TennisPlayer> activePlayers = roster.getActivePlayers();
        assertEquals(before + 1, activePlayers.size());
        assertTrue(activePlayers.contains(human));
    }

    /**
     * Sprawdza, czy co rok usuwa się stare boty i uzupełnia listę do docelowego rozmiaru.
     */
    @Test
    public void processYearlyEvolution_keepsRosterSizeAndReturnsRetirements() {
        WorldTourRoster roster = new WorldTourRoster();
        int initialSize = roster.getActivePlayers().size();

        int retiredCount = roster.processYearlyEvolution();

        assertEquals(initialSize + 1, roster.getActivePlayers().size());
        assertTrue(retiredCount >= 0);
    }

    /**
     * Sprawdza, czy symulacja meczów botów w tle utrzymuje listę posortowaną po punktach.
     */
    @Test
    public void simulateBackgroundMatches_keepsSortedRoster() {
        WorldTourRoster roster = new WorldTourRoster();

        roster.simulateBackgroundMatches();

        ObservableList<TennisPlayer> sorted = roster.getActivePlayers();
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1).getRankingPoints() >= sorted.get(i).getRankingPoints());
        }
    }
}
