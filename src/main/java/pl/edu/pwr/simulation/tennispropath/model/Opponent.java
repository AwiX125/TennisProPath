package pl.edu.pwr.simulation.tennispropath.model;

/**
 * Klasa reprezentująca przeciwnika (bota) w świecie gry
 * Dziedziczy po klasie abstrakcyjnej TennisPlayer, uzupełniając ją o jawne
 * przypisanie wylosowanych przez fabrykę statystyk technicznych oraz fizycznych
 */
public class Opponent extends TennisPlayer {

    /**
     * Konstruktor tworzący kompletny obiekt przeciwnika
     * Przekazuje podstawowe dane profilowe do klasy bazowej oraz inicjalizuje
     * szczegółowe statystyki uderzeń i adaptacji do nawierzchni
     *
     * @param name             Imię i nazwisko wygenerowanego bota
     * @param skillLevel       Poziom umiejętności determinujący klasę i pozycję startową w rankingu
     * @param age              Wiek zawodnika w momencie wejścia do puli ATP
     * @param forehandStrength Statystyka uderzenia z forehandu
     * @param backhandStrength Statystyka uderzenia z backhandu
     * @param serveStrength    Statystyka siły i celności serwisu
     * @param grassStrength    Współczynnik adaptacji do gry na trawie
     * @param clayStrength     Współczynnik adaptacji do gry na mączce
     * @param hardStrength     Współczynnik adaptacji do gry na betonie
     */
    public Opponent(String name, int skillLevel, int age, double forehandStrength, double backhandStrength, double serveStrength, double grassStrength, double clayStrength, double hardStrength) {
        // Wywołanie konstruktora klasy bazowej TennisPlayer
        super(name, skillLevel, age);

        // Przypisanie szczegółowych atrybutów tenisowych bota
        this.forehandStrength = forehandStrength;
        this.backhandStrength = backhandStrength;
        this.serveStrength = serveStrength;

        // Przypisanie indywidualnych predyspozycji bota do konkretnych typów nawierzchni
        this.grassStrength = grassStrength;
        this.clayStrength = clayStrength;
        this.hardStrength = hardStrength;
    }
}