package pl.edu.pwr.simulation.tennispropath.model;

import java.util.Random;

/**
 * System zarządzania kontuzjami (InjurySystem)
 * Odpowiada za kalkulację poziomu zmęczenia zawodnika, losowanie szansy na odniesienie
 * urazu z przeciążenia oraz definiowanie czasu trwania rekonwalescencji
 */
public class InjurySystem {
    // Wspólny generator liczb pseudolosowych dla obliczeń probabilistycznych
    private final Random random = new Random();

    private static final int CHANCE_LOW_FATIGUE = 2;  // Niskie zmęczenie: 2% szansy
    private static final int CHANCE_MEDIUM_FATIGUE = 8; // Umiarkowane zmęczenie: 8% szansy
    private static final int CHANCE_HIGH_FATIGUE = 15; // Odczuwalne zmęczenie: 15% szansy
    private static final int CHANCE_CRITICAL_FATIGUE = 30;  // Silne zmęczenie: 30% szansy
    private static final int CHANCE_MAX_FATIGUE = 45; // Skrajne wycieńczenie: 45% szansy

    /**
     * Wykonuje losowanie kontuzji na podstawie aktualnego zmęczenia
     * Przelicza poziom zmęczenia na bazową szansę procentową, nakłada czynnik losowy
     * i sprawdza, czy zawodnik ulega kontuzji.
     *
     * @param fatigue Obliczony wcześniej współczynnik zmęczenia zawodnika
     * @return true jeśli wylosowano kontuzję, false w przeciwnym wypadku
     */
    public boolean rollForInjury(double fatigue) {
        // Normalizacja wartości zmęczenia do bezpiecznego przedziału [0.0, 1.0]
        double normalizedFatigue = Math.max(0.0, Math.min(1.0, fatigue));

        // Przypisanie bazowej szansy w zależności od progu zmęczenia
        int baseChance;

        if (normalizedFatigue < 0.3) {
            baseChance = CHANCE_LOW_FATIGUE;
        } else if (normalizedFatigue < 0.5) {
            baseChance = CHANCE_MEDIUM_FATIGUE;
        } else if (normalizedFatigue < 0.7) {
            baseChance = CHANCE_HIGH_FATIGUE;
        } else if (normalizedFatigue < 0.9) {
            baseChance = CHANCE_CRITICAL_FATIGUE;
        } else {
            baseChance = CHANCE_MAX_FATIGUE;
        }

        // Dodanie elementu nieprzewidywalności (czysty przypadek/pech) z zakresu od 0 do 20%
        int volatility = random.nextInt(21);

        // Zsumowanie szansy bazowej i losowej, ograniczając wynik do maksymalnie 100%
        int chancePercent = Math.min(100, baseChance + volatility);

        // Ostateczny test losowy - sprawdzenie czy losowa liczba [0-99] jest mniejsza od wyliczonego procentu
        return random.nextInt(100) < chancePercent;
    }


    /**
     * Oblicza poziom zmęczenia na podstawie energii i wytrzymałości
     * Reprezentuje stopień wyeksploatowania organizmu zawodnika w skali od 0.0 do 1.0
     *
     * @param energy  Aktualny poziom energii życiowej zawodnika
     * @param stamina Maksymalna aktualna wytrzymałość organizmu
     * @return Wartość double z zakresu [0.0 - pełny wypoczynek] do [1.0 - skrajne wycieńczenie]
     */
    public double calculateFatigue(double energy, double stamina) {
        // Zabezpieczenie przed dzieleniem przez zero lub wartościami ujemnymi staminy (np. u skrajnie starych botów)
        if (stamina <= 0.0) {
            return 1.0; // Brak staminy oznacza permanentne, maksymalne zmęczenie
        }

        // Zmęczenie to dopełnienie stosunku aktualnej energii do maksymalnej wytrzymałości
        double fatigue = 1.0 - (energy / stamina);

        // Ponowna ucieczka przed błędami zaokrągleń - sztywne ucięcie wyniku do ram [0.0, 1.0]
        return Math.max(0.0, Math.min(1.0, fatigue));
    }

    /**
     * Określa czas powrotu do zdrowia dla kontuzji na podstawie poziomu zmęczenia
     * Im bardziej wycieńczony organizm w momencie urazu, tym dłuższa rekonwalescencja
     *
     * @param fatigue Współczynnik zmęczenia, przy którym doszło do wypadku
     * @return Liczba tygodni (int), jaką zawodnik musi spędzić na rehabilitacji
     */
    public int determineRecoveryDuration(double fatigue) {
        // Normalizacja wartości zmęczenia do przedziału [0.0, 1.0]
        double normalizedFatigue = Math.max(0.0, Math.min(1.0, fatigue));
        int baseWeeks; // Zmienna na bazowy czas trwania kontuzji w tygodniach

        // Przypisanie bazowej długości leczenia na podstawie progów zmęczenia
        if (normalizedFatigue < 0.3) {
            baseWeeks = 1;   // Lekki uraz: 1 tydzień przerwy
        } else if (normalizedFatigue < 0.5) {
            baseWeeks = 2;   // Prosta kontuzja: 2 tygodnie przerwy
        } else if (normalizedFatigue < 0.7) {
            baseWeeks = 3;   // Średni uraz: 3 tygodnie przerwy
        } else if (normalizedFatigue < 0.9) {
            baseWeeks = 4;   // Poważna kontuzja: 4 tygodnie przerwy
        } else {
            baseWeeks = 5;   // Ciężki uraz mięśniowy/stawowy: 5 tygodni przerwy
        }

        // Dodanie losowego przedłużenia leczenia o 0 lub 1 tydzień (np. komplikacje w rehabilitacji)
        return baseWeeks + random.nextInt(2);
    }
}