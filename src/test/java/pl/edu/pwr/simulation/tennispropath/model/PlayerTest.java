package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `Player` - zapis wyników i podstawowe zachowania energetyczne.
 */
public class PlayerTest {

    /**
     * Weryfikuje, że recordMatchResult aktualizuje liczniki rozegranych i wygranych meczy oraz że getWinRate zwraca poprawny procent zwycięstw.
     */
    @Test
    public void recordMatchResult_andWinRate_workCorrectly() {
        Player p = new Player("Hubert");
        assertEquals(0, p.getMatchesPlayed());
        assertEquals(0, p.getMatchesWon());

        p.recordMatchResult(true);
        assertEquals(1, p.getMatchesPlayed());
        assertEquals(1, p.getMatchesWon());
        assertEquals(100.0, p.getWinRate(), 0.000000001);

        p.recordMatchResult(false);
        assertEquals(2, p.getMatchesPlayed());
        assertEquals(1, p.getMatchesWon());
        assertEquals(50.0, p.getWinRate(), 0.000000001);
    }

    /**
     * Sprawdza, czy ustawienie energii powyżej staminy jest niemożliwe.
     */
    @Test
    public void setEnergy_isClampedToStamina() {
        Player p = new Player("Iga");
        p.setEnergy(1000.0);
        assertTrue(p.getEnergy() <= p.getStamina());
    }

    /**
     * Sprawdza, czy odpoczynek podnosi energię i nie pozwala jej przekroczyć staminy.
     */
    @Test
    public void rest_recoversEnergyWithoutExceedingStamina() {
        Player p = new Player("Robert");
        p.setEnergy(90.0);

        p.rest();

        assertEquals(p.getStamina(), p.getEnergy(), 0.000000001);
    }

    /**
     * Sprawdza, czy po 30. urodzinach stamina spada o 5 punktów.
     */
    @Test
    public void celebrateBirthday_reducesStaminaAfterThirty() {
        Player p = new Player("Maja");
        p.setAge(30);

        p.celebrateBirthday();

        assertEquals(31, p.getAge());
        assertEquals(95.0, p.getStamina(), 0.000000001);
    }

    /**
     * Sprawdza, czy przy zerowej energii poziom zmęczenia jest maksymalny.
     */
    @Test
    public void getFatigueLevel_zeroEnergy_returnsOne() {
        Player p = new Player("Robert");
        p.setEnergy(0.0);

        assertEquals(1.0, p.getFatigueLevel(), 0.000000001);
    }

    /**
     * Sprawdza, czy przy niskiej energii metoda act wybiera odpoczynek.
     */
    @Test
    public void act_restoresEnergyWhenLow() {
        Player p = new Player("Hubert");
        p.setEnergy(20.0);

        double before = p.getEnergy();
        p.act();

        assertTrue(p.getEnergy() > before);
    }

    /**
     * Sprawdza, czy przy wysokiej energii metoda act wybiera trening.
     */
    @Test
    public void act_trainsWhenEnergyIsHigh() {
        Player p = new Player("Iga");
        p.setEnergy(100.0);

        double before = p.getEnergy();
        p.act();

        assertTrue(p.getEnergy() < before);
    }
}
