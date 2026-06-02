package pl.edu.pwr.simulation.tennispropath.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import pl.edu.pwr.simulation.tennispropath.model.CourtType;
import pl.edu.pwr.simulation.tennispropath.model.*;

/**
 * Główny kontroler aplikacji (SimulationController)
 * Odpowiada za zarządzanie warstwą prezentacji (GUI) głównego okna symulacji, wiązanie kontrolek
 * JavaFX z danymi domenowymi, kontrolę asynchronicznego zegara symulacji (Timeline)
 * oraz sekwencyjne odświeżanie stanu prezentowanego użytkownikowi w każdym tygodniu kariery
 */
public class SimulationController {

    // =========================================================================
    // KONTROLKI FXML - SEKCOJA PROFILU GŁÓWNEGO ZAWODNIKA
    // =========================================================================
    @FXML private Label ageLabel;        // Etykieta tekstowa wyświetlająca aktualny wiek gracza
    @FXML private Label xpLabel;         // Etykieta paska doświadczenia (format: aktualny / wymagany)
    @FXML private Label favCourtLabel;   // Etykieta wskazująca preferowaną, ulubioną nawierzchnię gracza
    @FXML private ProgressBar staminaBar;// Graficzny pasek postępu reprezentujący stan energii organizmu
    @FXML private Label rankingLabel;    // Etykieta prezentująca aktualną liczbę punktów rankingowych ATP
    @FXML private Label statusLabel;     // Status medyczny zawodnika ( dynamicznie stylizowany Zdrowy / Kontuzjowany )
    @FXML private Label skillLabel;      // Etykieta wyświetlająca ogólny poziom skilla (Skill Level) zawodnika

    // =========================================================================
    // KONTROLKI FXML - WSPÓŁCZYNNIKI ADAPTACJI DO NAWIERZCHNI
    // =========================================================================
    @FXML private Label clayLabel;       // Etykieta siły gry i poruszania się na mączce
    @FXML private Label grassLabel;      // Etykieta siły gry i poruszania się na trawie
    @FXML private Label hardLabel;       // Etykieta siły gry i poruszania się na betonie

    // =========================================================================
    // KONTROLKI FXML - STATYSTYKI GŁÓWNYCH UDERZEŃ TENISOWYCH
    // =========================================================================
    @FXML private Label forehandLabel;   // Etykieta precyzji, rotacji i siły zagrania z forehandu
    @FXML private Label backhandLabel;   // Etykieta precyzji, powtarzalności i siły zagrania z backhandu
    @FXML private Label serveLabel;      // Etykieta efektywności, rotacji oraz surowej siły serwisu

    // =========================================================================
    // KONTROLKI FXML - BILANS KARIERY I STATYSTYKI MECZOWE
    // =========================================================================
    @FXML private Label winsLabel;       // Łączna liczba wygranych pojedynków singlowych w karierze
    @FXML private Label lossesLabel;     // Łączna liczba przegranych pojedynków singlowych w karierze
    @FXML private Label winRateLabel;    // Procentowy współczynnik zwycięstw (Win Rate) obliczany z sumy meczów

    // =========================================================================
    // KONTROLKI FXML - OGÓLNY PANEL STEROWANIA I MONITOROWANIA TIME-LINE
    // =========================================================================
    @FXML private Label timeLabel;           // Etykieta wskazująca bieżący tydzień kariery zawodnika (chronologia gry)
    @FXML private Label nameLabel;           // Etykieta prezentująca imię i nazwisko profilu gracza ludzkiego
    @FXML private Label actionLabel;         // Tekstowa informacja o ostatnim podjętym działaniu (logika autonomiczna/mecz)
    @FXML private Button simulationButton;   // Przycisk Start/Stop zarządzający stanem odtwarzania osi czasu
    @FXML private TextArea logArea;          // Główne okno kroniki (logów) zdarzeń realizujące ciągłe dopisywanie komunikatów
    @FXML private Slider speedSlider;        // Suwak do płynnej, interaktywnej regulacji prędkości upływu czasu
    @FXML private Label injuryWeeksLabel;    // Liczba pozostałych tygodni rekonwalescencji i rehabilitacji przy kontuzji
    @FXML private Label energyLabel;         // Liczbowy, precyzyjny podgląd energii (format: aktualna / maksymalna stamina)

    // =========================================================================
    // KONTROLKI FXML - GLOBALNA TABELA RANKINGOWA WORLD TOUR ATP
    // =========================================================================
    @FXML private TableView<TennisPlayer> rosterTable;         // Widok tabelaryczny przechowujący listę wszystkich aktywnych graczy
    @FXML private TableColumn<TennisPlayer, String> nameCol;   // Kolumna tabeli: Imię i nazwisko zawodnika
    @FXML private TableColumn<TennisPlayer, Integer> ageCol;   // Kolumna tabeli: Wiek tenisisty
    @FXML private TableColumn<TennisPlayer, Integer> skillCol; // Kolumna tabeli: Poziom ogólny (Skill Level)
    @FXML private TableColumn<TennisPlayer, Integer> rankingPoints; // Kolumna tabeli: Aktualny dorobek punktów rankingowych ATP

    // =========================================================================
    // POLA DOMENOWE I LOGIKA MODELU SYSTEmU
    // =========================================================================
    private Player player;                      // Obiekt żywego gracza reprezentujący postać sterowaną przez użytkownika
    private WorldTourRoster worldTourRoster;    // Menedżer bazy danych przechowujący i zarządzający całą ligą ATP (ludźmi i botami)
    private final MatchEngine matchEngine = new MatchEngine(); // Silnik obliczeniowy symulujący punktowe i setowe pojedynki tenisowe

    // Menedżer czasu JavaFX (Timeline) realizujący asynchroniczne, cykliczne wykonywanie kroków gry w tle
    private Timeline timeline;
    private int currentWeek = 1;                // Licznik chronologiczny odliczający czas gry w jednostkach tygodniowych
    private boolean isRunning = false;          // Flaga stanu informująca środowisko, czy pętla symulacji jest w tym momencie uruchomiona

    /**
     * Metoda automatycznie wywoływana przez kontener JavaFX po pomyślnym załadowaniu struktury pliku FXML
     * Odpowiada za wstępne spięcie kolumn tabeli z polami obiektów za pomocą mechanizmu refleksji,
     * powołanie globalnego rosteru ligowego oraz konfigurację suwaka prędkości i początkowej osi czasu
     */
    @FXML
    public void initialize() {
        // Konfiguracja kolumn Tabeli ATP przy użyciu refleksji (PropertyValueFactory automatycznie wyszukuje gettery w klasie TennisPlayer)
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        skillCol.setCellValueFactory(new PropertyValueFactory<>("skillLevel"));
        rankingPoints.setCellValueFactory(new PropertyValueFactory<>("rankingPoints"));

        // Inicjalizacja klasy zarządzającej pełną pulą tenisistów i powiązanie jej kolekcji z widokiem tabeli (ObservableList)
        this.worldTourRoster = new WorldTourRoster();
        rosterTable.setItems(worldTourRoster.getActivePlayers());

        // Konfiguracja startowej prędkości zegara symulacji (matematyczne odwrócenie wartości z suwaka na milisekundy opóźnienia wątku)
        double invertedStartSpeed = (1000.0 + 5.0) - speedSlider.getValue();
        setupTimeline(invertedStartSpeed);

        // Rejestracja nasłuchiwania (Listener) zmian wartości prędkości na suwaku w czasie rzeczywistym
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double invertedMiliseconds = (1000.0 + 5.0) - newValue.doubleValue();
            setupTimeline(invertedMiliseconds);
        });
    }

    /**
     * Konfiguruje nową lub aktualizuje bieżącą instancję zegara symulacji dla określonej liczby milisekund
     * Zapewnia płynne przejście między interwałami bez resetowania stanu działania symulacji
     *
     * @param ms Czas trwania jednego kroku (odpowiadający jednemu tygodniowi w świecie gry) w milisekundach
     */
    private void setupTimeline(double ms) {
        boolean resubmit = isRunning; // Zapamiętanie stanu (czy zegar pracował przed modyfikacją prędkości)

        if (timeline != null) {
            timeline.stop(); // Bezpieczne zatrzymanie i wyrejestrowanie poprzedniej instancji osi czasu
        }

        // Powołanie nowej osi czasu (Timeline) z klatką kluczową wykonującą metodę nextStep() w zadanym interwale
        timeline = new Timeline(new KeyFrame(Duration.millis(ms), event -> nextStep()));
        timeline.setCycleCount(Animation.INDEFINITE); // Ustawienie pętli na nieskończoną liczbę powtórzeń (aż do jawnego zatrzymania)

        if (resubmit) {
            timeline.play();
        }
    }

    /**
     * Główna pętla symulacji – wywoływana asynchronicznie przez wątek JavaFX w każdym kroku osi czasu
     * Odpowiada za inkrementację czasu, obsługę corocznej wymiany kadr w Tourze, awanse poziomów
     * doświadczenia (Level Up), sterowanie autonomiczną logiką działań gracza oraz symulację meczów w tle
     */
    private void nextStep() {
        currentWeek++; // Upływ kolejnego tygodnia chronologicznego w świecie gry
        String action; // Zmienna pomocnicza przechowująca opis tekstowy podjętej w bieżącym kroku akcji

        // Coroczne starzenie świata gry (wywoływane cyklicznie co 52 tygodnie)
        if (currentWeek % 52 == 0) {
            player.celebrateBirthday(); // Urodziny żywego zawodnika (implikujące m.in. ewentualny regres staminy)
            logEvent("Zawodnik świętuje urodziny. Ma teraz " + player.getAge() + " lat.");

            // Przetworzenie wymiany pokoleniowej botów w rankingu (emerytury starszych graczy, generowanie juniorów)
            int retiredCount = worldTourRoster.processYearlyEvolution();

            if (retiredCount > 0) {
                logEvent("🔄 ATP Tour: " + retiredCount + " zawodników odeszło na emeryturę. Ich miejsce zajęli nowi juniorzy!");
            }
        }

        // Reakcja na awans poziomu doświadczenia zawodnika (Level Up)
        if (player.isLeveledUp()) {
            logEvent("AWANS! " + player.getName() + " wskoczył na poziom " + player.getSkillLevel() + ". Kolejny poziom wymaga: " + player.getXpRequiredForNextLevel() + " XP.");
            player.setLeveledUp(false);
        }

        // Harmonogram rozgrywek (Tydzień turniejowy i Tydzień wolny)
        if (currentWeek % 4 == 0 && !player.isInjured()) {
            // Tydzień meczowy (co 4 tygodnie): Dobór sprawiedliwego rywala o podobnym skillu i symulacja spotkania
            Opponent bot = worldTourRoster.findMatchOpponent(player.getSkillLevel(), player);
            action = matchEngine.simulateMatch(player, bot);
        } else {
            // Tydzień wolny: Autonomiczna decyzja obiektu gracza o podjęciu treningu lub odpoczynku (zależna od zmęczenia)
            double oldEnergy = player.getEnergy();
            boolean oldInjury = player.isInjured();

            player.act();

            if (player.isInjured()) {
                action = oldInjury ? "Rehabilitacja kontuzji" : "Doznał kontuzji z przemęczenia!";
            } else if (player.getEnergy() > oldEnergy) {
                action = "Odpoczynek i regeneracja";
            } else {
                action = "Intensywny trening";
            }
        }

        // Aktualizacja życia ligowego w tle
        // Symulacja losowych, algorytmicznych pojedynków pomiędzy botami, zapewniająca ciągłą rotację punktów rankingowych
        worldTourRoster.simulateBackgroundMatches();

        // Ponowne posortowanie listy według kryterium punktów rankingowych i odświeżenie graficzne komponentu TableView
        worldTourRoster.sortRoster();
        rosterTable.refresh();

        // Zapisanie wygenerowanego zdarzenia w logach oraz aktualizacja całego interfejsu graficznego
        logEvent(action);
        updateUI(action);
    }

    /**
     * Pomocnicza metoda formatująca i dopisująca nowy rekord tekstowy na koniec komponentu TextArea
     *
     * @param message Komunikat tekstowy opisujący zdarzenie do zalogowania
     */
    private void logEvent(String message) {
        String logEntry = "[Tydzień " + currentWeek + "] " + message + "\n";
        logArea.appendText(logEntry);
    }

    /**
     * Inicjalizuje zaawansowany obiekt spersonalizowanego gracza na podstawie danych z kreatora postaci
     * Konfiguruje tożsamość, metrykę wieku, preferencje nawierzchni oraz rozdzielone punkty atrybutów,
     * a następnie wprowadza zawodnika bezpośrednio do globalnej tabeli rankingu
     * Metoda wywoływana z poziomu ConfigController w nowej wersji kreatora
     */
    public void initCustomPlayer(String name, int age, CourtType favoriteCourt, double fh, double bh, double serve) {
        // Tworzenie nowej instancji gracza z wybranym imieniem dedykowanym
        this.player = new Player(name);

        // Ustawianie parametrów wieku oraz preferowanej, ulubionej nawierzchni
        this.player.setAge(age);
        this.player.setFavoriteCourt(favoriteCourt);

        // Przypisywanie rozdanych punktów umiejętności poprzez dedykowane settery
        this.player.setForehandStrength(fh);
        this.player.setBackhandStrength(bh);
        this.player.setServeStrength(serve);

        // Adaptacja startowa do nawierzchni (ulubiony kort otrzymuje wyższy, uprzywilejowany bonus startowy)
        this.player.setClayStrength(favoriteCourt == CourtType.CLAY ? 25.0 : 15.0);
        this.player.setGrassStrength(favoriteCourt == CourtType.GRASS ? 25.0 : 15.0);
        this.player.setHardStrength(favoriteCourt == CourtType.HARD ? 25.0 : 15.0);

        // Rejestracja zawodnika we wspólnym zestawieniu ATP rankingu i odświeżenie komponentów GUI
        worldTourRoster.registerHumanPlayer(this.player);
        updateUI("Zawodnik stworzony pomyślnie. Oczekiwanie na start.");
    }

    /**
     * Akcja FXML obsługująca interaktywny przycisk uruchamiania i zatrzymywania zegara symulacji
     * Dynamicznie podmienia etykiety tekstowe oraz kompletne style CSS (kolorystykę tła, ramki, cienie) przycisku
     */
    @FXML
    protected void onToggleSimulation() {
        if (isRunning) {
            timeline.stop(); // Bezpieczne wstrzymanie odliczania zegara osi czasu JavaFX
            simulationButton.setText("WZNÓW SYMULACJĘ");
            simulationButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(16,185,129,0.4), 15, 0, 0, 0);");
        } else {
            timeline.play(); // Uruchomienie odtwarzania zegara osi czasu JavaFX
            simulationButton.setText("ZATRZYMAJ SYMULACJĘ");
            simulationButton.setStyle("-fx-background-color: #C24A6A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(185,16,129,0.4), 15, 0, 0, 0);");
        }
        isRunning = !isRunning;
    }

    /**
     * Główna metoda aktualizująca stan komponentów GUI w oparciu o aktualne dane modelu
     * przeprowadza formatowanie liczbowe do określonych miejsc po przecinku,
     * oblicza poziomy na paskach postępu oraz nakłada dynamiczne style kolorystyczne dla statusów
     *
     * @param currentAction Opis tekstowy ostatniego działania, przekazywany do głównej etykiety informacyjnej stanu
     */
    private void updateUI(String currentAction) {
        // Ogólne informacje o bieżącym stanie, czasie symulacji oraz nagłówkach profilu
        nameLabel.setText(player.getName());
        timeLabel.setText("Tydzień kariery: " + currentWeek);
        actionLabel.setText("Aktualne działanie: " + currentAction);

        // Profil główny zawodnika, aktualne doświadczenie, wiek oraz zasoby punktowe
        skillLabel.setText(String.valueOf(player.getSkillLevel()));
        double energyProgress = player.getEnergy() / player.getStamina(); // Obliczenie matematycznego ułamka energii organizmu [0.0 - 1.0]
        staminaBar.setProgress(energyProgress); // Przypisanie współczynnika wypełnienia paska ProgressBar JavaFX
        xpLabel.setText(player.getXpForCurrentLevel() + " XP / " + player.getXpRequiredForNextLevel() + " XP");
        ageLabel.setText(player.getAge() + " lat");
        rankingLabel.setText(player.getRankingPoints() + " pkt");
        energyLabel.setText(String.format("%.0f / %.0f", player.getEnergy(), player.getStamina())); // Sformatowany tekst podglądu numerycznego staminy

        // Statystyki bilansu kariery, liczby pojedynków i efektywności procentowej gry
        winsLabel.setText(String.valueOf(player.getMatchesWon()));
        lossesLabel.setText(String.valueOf(player.getMatchesLost()));
        winRateLabel.setText(String.format("%.0f%%", player.getWinRate()));

        // Monitorowanie stanu zdrowia zawodnika oraz dynamiczne, warunkowe kolorowanie statusów
        if (player.isInjured()) {
            statusLabel.setText("Status: Kontuzjowany");
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#ef4444"));
            injuryWeeksLabel.setText(player.getInjuryWeeksRemaining() + " tyg.");
        } else {
            statusLabel.setText("Status: Zdrowy");
            statusLabel.setStyle("-fx-text-fill: #a3e635; -fx-font-weight: bold;");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#22c55e"));
            injuryWeeksLabel.setText("Brak");
        }

        // Pobranie i wyświetlenie sformatowanej, czytelnej nazwy ulubionej nawierzchni kortu
        if (player.getFavoriteCourt() != null) {
            favCourtLabel.setText(player.getFavoriteCourt().getDisplayName());
        }

        // Statystyki szczegółowe uderzeń tenisowych (sformatowane i zaokrąglone do 1 miejsca po przecinku)
        forehandLabel.setText(String.format("%.1f", player.getForehandStrength()));
        backhandLabel.setText(String.format("%.1f", player.getBackhandStrength()));
        serveLabel.setText(String.format("%.1f", player.getServeStrength()));

        // Atrybuty adaptacji i specyficznych współczynników dla konkretnych typów nawierzchni turniejowych
        clayLabel.setText(String.format("%.1f", player.getClayStrength()));
        grassLabel.setText(String.format("%.1f", player.getGrassStrength()));
        hardLabel.setText(String.format("%.1f", player.getHardStrength()));
    }
}