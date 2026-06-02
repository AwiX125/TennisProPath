package pl.edu.pwr.simulation.tennispropath.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Klasa menedżera zestawienia World Tour (WorldTourRoster).
 * Odpowiada za utrzymanie listy aktywnych tenisistów, rejestrację żywego gracza,
 * obsługę rocznej ewolucji touru (emerytury i napływ nowych botów), dobór przeciwników
 * oraz symulowanie autonomicznych meczów w tle.
 */
public class WorldTourRoster {

    // Dynamiczna lista obserwowalna (ObservableList) spięta bezpośrednio z tabelą GUI
    // Przechowuje obiekty typu bazowego TennisPlayer, umożliwiając jednoczesne trzymanie obiektów Player oraz Opponent
    private final ObservableList<TennisPlayer> activePlayers = FXCollections.observableArrayList();

    // Fabryka odpowiedzialna za powoływanie do życia nowych botów
    private final OpponentFactory opponentFactory = new OpponentFactory();

    // Wspólny generator liczb pseudolosowych dla logiki losowania meczów i rywali
    private final Random random = new Random();

    // Stały, docelowy rozmiar puli zawodników (50 botów sterowanych przez komputer + 1 "żywy" użytkownik)
    private final int targetRosterSize = 31;

    /**
     * Konstruktor inicjalizujący zestawienie World Tour.
     * Na starcie generuje zestaw 50 początkowych botów za pomocą fabryki.
     */
    public WorldTourRoster() {
        // Generowanie 50 początkowych botów i dodanie ich do listy obserwowalnej
        for (int i = 0; i < targetRosterSize - 1; i++) {
            activePlayers.add(opponentFactory.createGlobalTourPlayer());
        }
    }

    /**
     * Rejestruje żywego gracza (Player) w globalnym rankingu.
     * Zapobiega duplikacji obiektu na liście, a po dodaniu gracza wymusza ponowne sortowanie.
     *
     * @param human Obiekt zalogowanego użytkownika
     */
    public void registerHumanPlayer(Player human) {
        if (!activePlayers.contains(human)) {
            activePlayers.add(human);
        }
        sortRoster(); // Przeorganizowanie tabeli po dodaniu nowego uczestnika
    }

    /**
     * Zwraca listę powiązaną z GUI TableView.
     * Pozwala kontrolerowi SimulationController ustawić elementy tabeli rankingu.
     */
    public ObservableList<TennisPlayer> getActivePlayers() {
        return activePlayers;
    }

    /**
     * Logika rocznej aktualizacji - starzenie, emerytury, nowe talenty
     * Wywoływana raz na rok (co 52 tygodnie). Starzeje wszystkich botów, usuwa tych,
     * którzy przekroczyli 45 lat i uzupełnia braki nowymi juniorami
     *
     * @return Liczba botów, którzy odeszli w tym roku na emeryturę (int)
     */
    public int processYearlyEvolution() {
        int retiredCount = 0;
        // Użycie Iteratora w celu bezpiecznego usuwania elementów z kolekcji podczas pętli
        Iterator<TennisPlayer> iterator = activePlayers.iterator();

        while (iterator.hasNext()) {
            TennisPlayer player = iterator.next();

            // Gracz jest objęty procesem "ewolucji" w klasie SimulationController
            if (player instanceof Player) {
                continue;
            }

            // Logika starzenia i emerytury wyłącznie dla botów (Opponent)
            player.ageUp();
            if (player.getAge() > 45) {
                iterator.remove(); // Usunięcie bota z listy obserwowalnej
                retiredCount++;    // Inkrementacja licznika emerytów
            }
        }

        // Uzupełnianie brakujących botów do stałej wielkości touru (targetRosterSize)
        // Nowo generowani zawodnicy imitują napływ świeżych talentów (juniorów) do rankingu
        while (activePlayers.size() < targetRosterSize) {
            activePlayers.add(opponentFactory.createGlobalTourPlayer());
        }

        sortRoster(); // Ponowne ułożenie tabeli po zmianach kadrowych
        return retiredCount;
    }

    /**
     * Dobiera sprawiedliwego rywala z puli
     * W pierwszej kolejności szuka botów, których Skill Level różni się od skilla gracza o maksymalnie 10 punktów
     * W przypadku braku dopasowania (fallback), przeszukuje całą dostępną pulę botów
     *
     * @param playerSkill  Aktualny poziom umiejętności żywego gracza
     * @param humanPlayer  Instancja obiektu żywego gracza
     * @return Wylosowany obiekt klasy Opponent spełniający kryteria
     */
    public Opponent findMatchOpponent(int playerSkill, Player humanPlayer) {
        List<Opponent> suitableOpponents = new ArrayList<>();

        // Krok 1: Próba znalezienia sprawiedliwego rywala w zbliżonym przedziale umiejętności
        for (TennisPlayer tp : activePlayers) {
            if (tp instanceof Opponent bot) {
                // Sprawdzenie czy różnica umiejętności jest mniejsza bądź równa 10
                if (Math.abs(bot.getSkillLevel() - playerSkill) <= 10) {
                    suitableOpponents.add(bot);
                }
            }
        }

        // Krok 2: Fallback - jeśli nie ma nikogo w optymalnym przedziale skillowym bierzemy jakiegokolwiek bota
        if (suitableOpponents.isEmpty()) {
            for (TennisPlayer tp : activePlayers) {
                if (tp instanceof Opponent bot) {
                    suitableOpponents.add(bot);
                }
            }
        }

        // Zwrócenie losowego bota ze zgromadzonej listy dopuszczalnych rywali
        return suitableOpponents.get(random.nextInt(suitableOpponents.size()));
    }

    /**
     * Symuluje losowe mecze między botami w tle, aby ich punkty rankingowe stale się zmieniały
     */
    public void simulateBackgroundMatches() {
        List<Opponent> botsOnly = activePlayers.stream()
                .filter(tp -> tp instanceof Opponent)
                .map(tp -> (Opponent) tp)
                .toList();

        // jeżeli pula botów będzie za mała, koniec symulowania meczy przeciwników
        if (botsOnly.size() < 2) return;

        // Losowanie ile meczów odbędzie się w tym tygodniu (od 3 do 7 pojedynków)
        int matchesCount = 3 + random.nextInt(5);

        for (int i = 0; i < matchesCount; i++) {
            Opponent p1 = botsOnly.get(random.nextInt(botsOnly.size()));
            Opponent p2 = botsOnly.get(random.nextInt(botsOnly.size()));

            // tenisista nie może grać z samym sobą
            if (p1 == p2) {
                continue;
            }

            // Obliczanie szansy na wygraną na podstawie poziomu umiejętności obu botów
            int totalSkill = p1.getSkillLevel() + p2.getSkillLevel();
            int roll = random.nextInt(totalSkill);

            // Wyłonienie zwycięzcy i przegranego na podstawie proporcji ich poziomów skilla
            TennisPlayer winner = (roll < p1.getSkillLevel()) ? p1 : p2;
            TennisPlayer loser = (winner == p1) ? p2 : p1;

            // Obliczanie dynamicznej stawki punktowej na podstawie różnicy skilla
            int skillDiff = winner.getSkillLevel() - loser.getSkillLevel();
            int pointsAwarded = Math.max(15, 80 - (skillDiff * 2));
            int pointsLost = Math.max(5, pointsAwarded / 2);

            // Aktualizacja punktów konta rankingowego dla bota, który wygrał
            winner.addRankingPoints(pointsAwarded);

            // Aktualizacja punktów bota, który przegrał (z zabezpieczeniem przed spadkiem < 0)
            if (loser.getRankingPoints() - pointsLost < 0) {
                loser.setRankingPoints(0);
            } else {
                loser.addRankingPoints(-pointsLost);
            }
        }

        // Po rozegraniu wszystkich meczów w tle, sortowanie listy na nowo według aktualnych punktów
        sortRoster();
    }

    /**
     * Sortuje ranking od najwyższej do najniższej liczby punktów rankingowych
     * Wykorzystuje wyrażenie lambda do porównania pola rankingPoints obiektów TennisPlayer.
     */
    public void sortRoster() {
        activePlayers.sort((p1, p2) -> Integer.compare(p2.getRankingPoints(), p1.getRankingPoints()));
    }
}