package pl.edu.pwr.simulation.tennispropath.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy `RankingCategory` - fazowanie kategorii na podstawie punktów.
 */
public class RankingCategoryTest {

    /**
     * Sprawdza, że metoda fromPoints przypisuje właściwą kategorię rankingową dla punktów.
     */
    @Test
    public void fromPoints_returnsExpectedCategoryForBoundaryValues() {
        assertEquals(RankingCategory.BRONZE, RankingCategory.fromPoints(-10));
        assertEquals(RankingCategory.BRONZE, RankingCategory.fromPoints(0));
        assertEquals(RankingCategory.BRONZE, RankingCategory.fromPoints(999));

        assertEquals(RankingCategory.SILVER, RankingCategory.fromPoints(1000));
        assertEquals(RankingCategory.SILVER, RankingCategory.fromPoints(1999));

        assertEquals(RankingCategory.GOLD, RankingCategory.fromPoints(2000));
        assertEquals(RankingCategory.GOLD, RankingCategory.fromPoints(2999));

        assertEquals(RankingCategory.PLATINUM, RankingCategory.fromPoints(3000));
        assertEquals(RankingCategory.PLATINUM, RankingCategory.fromPoints(Integer.MAX_VALUE));
    }

    /**
     * Sprawdza, czy getDisplayName zwracają oczekiwane nazwy kategorii.
     */
    @Test
    public void getDisplayName_matchesExpectedLabels() {
        assertEquals("Bronze", RankingCategory.BRONZE.getDisplayName());
        assertEquals("Silver", RankingCategory.SILVER.getDisplayName());
        assertEquals("Gold", RankingCategory.GOLD.getDisplayName());
        assertEquals("Platinum", RankingCategory.PLATINUM.getDisplayName());
    }
}
