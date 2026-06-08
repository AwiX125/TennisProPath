package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `InjurySystem` - zmęczenie i czas rekonwalescencji.
 */
public class InjurySystemTest {

    private final InjurySystem injurySystem = new InjurySystem();
    private static final double EPS = 0.000001;

    /**
     * Sprawdza obliczenie poziomu zmęczenia przy normalnych wartościach energii i staminy.
     */
    @Test
    public void calculateFatigue_normalValues() {
        double fatigue = injurySystem.calculateFatigue(50.0, 100.0);
        assertEquals(0.5, fatigue, EPS);
    }

    /**
     * Sprawdza, czy przy zerowej staminie zwraca maksymalne zmęczenie.
     */
    @Test
    public void calculateFatigue_zeroStaminaReturnsMax() {
        double fatigue = injurySystem.calculateFatigue(10.0, 0.0);
        assertEquals(1.0, fatigue, EPS);
    }

    /**
     * Sprawdza, czy dla różnych poziomów zmęczenia czas rekonwalescencji mieści się
     * w oczekiwanych przedziałach.
     */
    @Test
    public void determineRecoveryDuration_withinExpectedBounds() {
        int low = injurySystem.determineRecoveryDuration(0.1);
        assertTrue(low >= 1 && low <= 2);

        int critical = injurySystem.determineRecoveryDuration(0.85);
        assertTrue(critical >= 4 && critical <= 6);

        int max = injurySystem.determineRecoveryDuration(0.99);
        assertTrue(max >= 5 && max <= 6);
    }
}
