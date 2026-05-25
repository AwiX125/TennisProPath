package pl.edu.pwr.simulation.tennispropath.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import pl.edu.pwr.simulation.tennispropath.model.MatchEngine;
import pl.edu.pwr.simulation.tennispropath.model.Opponent;
import pl.edu.pwr.simulation.tennispropath.model.OpponentFactory;
import pl.edu.pwr.simulation.tennispropath.model.Player;

public class SimulationController {


    // Profil
    @FXML private Label ageLabel;
    @FXML private Label xpLabel;
    @FXML private Label favCourtLabel;
    @FXML private ProgressBar staminaBar;
    @FXML private Label rankingLabel;
    @FXML private Label statusLabel;
    @FXML private Label skillLabel;

    // Kort
    @FXML private Label clayLabel;
    @FXML private Label grassLabel;
    @FXML private Label hardLabel;

    // Umiejętności
    @FXML private Label forehandLabel;
    @FXML private Label backhandLabel;
    @FXML private Label serveLabel;

    // Statystyki
    @FXML private Label winsLabel;
    @FXML private Label lossesLabel;
    @FXML private Label winRateLabel;

    // Ogólne
    @FXML private Label timeLabel;
    @FXML private Label nameLabel;
    @FXML private Label actionLabel;
    @FXML private Button simulationButton;
    @FXML private TextArea logArea;
    @FXML private Slider speedSlider;
    @FXML private Label injuryWeeksLabel;
    @FXML private Label energyLabel;

    // Player & Opponent
    private Player player;
    private final MatchEngine matchEngine = new MatchEngine();
    private final OpponentFactory opponentFactory = new OpponentFactory();

    private Timeline timeline;
    private int currentWeek = 1;
    private boolean isRunning = false;

    @FXML
    public void initialize() {
        double invertedStartSpeed = (1000.0 + 5.0) - 400.0;
        setupTimeline(invertedStartSpeed);
    }

    /**
     * Konfiguruje zegar symulacji dla określonej liczby milisekund
     */
    private void setupTimeline(double ms) {
        boolean resubmit = isRunning;

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(ms), event -> nextStep()));
        timeline.setCycleCount(Animation.INDEFINITE);

        if (resubmit) {
            timeline.play();
        }
    }

    /**
     * Metoda inicjalizuje obiekt Player z wybranym imieniem
     * i przygotowuje interfejs do wyświetlenia danych
     */
    public void setPlayerName(String name) {
        this.player = new Player(name);
        updateUI("Oczekiwanie na start");

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double currentSliderValue = newValue.doubleValue();

            double invertedMiliseconds = (1000.0 + 5.0) - currentSliderValue;

            setupTimeline(invertedMiliseconds);
        });
    }

    /**
     * Główna pętla symulacji – wywoływana automatycznie przez zegar
     */
    private void nextStep() {
        currentWeek++;
        String action;

        if (currentWeek % 52 == 0) {
            player.celebrateBirthday();
            logEvent("Zawodnik świętuje urodziny. Ma teraz " + player.getAge() + " lat.");
        }

        if (player.isLeveledUp()) {
            logEvent("AWANS! " + player.getName() + " wskoczył na poziom " + player.getSkillLevel() + ". Kolejny poziom wymaga: " + player.getXpRequiredForNextLevel() + " XP.");
            player.setLeveledUp(false);
        }

        if (currentWeek % 4 == 0 && !player.isInjured()) {
            Opponent bot = opponentFactory.createRandomOpponent(player.getSkillLevel());

            action = matchEngine.simulateMatch(player, bot);
        } else {
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

        logEvent(action);

        updateUI(action);
    }

    private void logEvent(String message) {
        String logEntry = "[Tydzień " + currentWeek + "] " + message + "\n";
        logArea.appendText(logEntry);
    }

    @FXML
    protected void onToggleSimulation() {
        if (isRunning) {
            timeline.stop();
            simulationButton.setText("WZNÓW SYMULACJĘ");
            simulationButton.setStyle("-fx-background-color: #a3e635; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 15 25; -fx-cursor: hand;");
        } else {
            timeline.play();
            simulationButton.setText("ZATRZYMAJ SYMULACJĘ");
            simulationButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 25; -fx-cursor: hand;");
        }
        isRunning = !isRunning;
    }

    private void updateUI(String currentAction) {
        // Ogólne
        nameLabel.setText(player.getName());
        timeLabel.setText("Tydzień kariery: " + currentWeek);
        actionLabel.setText("Aktualne działanie: " + currentAction);

        // Profil
        skillLabel.setText(String.valueOf(player.getSkillLevel()));
        double energyProgress = player.getEnergy() / player.getStamina();
        staminaBar.setProgress(energyProgress);
        xpLabel.setText(player.getXpForCurrentLevel() + " XP / " + player.getXpRequiredForNextLevel() + " XP");
        ageLabel.setText(player.getAge() + " lat");
        rankingLabel.setText(player.getRankingPoints() + " pkt");

        // Statystyki
        winsLabel.setText(String.valueOf(player.getMatchesWon()));
        lossesLabel.setText(String.valueOf(player.getMatchesLost()));
        winRateLabel.setText(String.format("%.0f%%", player.getWinRate()));

        energyLabel.setText(String.format("%.0f / %.0f", player.getEnergy(), player.getStamina()));

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

        // Ulubiony kort (używamy pobierania nazwy z Enuma CourtType)
        if (player.getFavoriteCourt() != null) {
            favCourtLabel.setText(player.getFavoriteCourt().getDisplayName());
        }

        // umiejętności
        forehandLabel.setText(String.format("%.1f", player.getForehandStrength()));
        backhandLabel.setText(String.format("%.1f", player.getBackhandStrength()));
        serveLabel.setText(String.format("%.1f", player.getServeStrength()));

        // korty
        clayLabel.setText(String.format("%.1f", player.getClayStrength()));
        grassLabel.setText(String.format("%.1f", player.getGrassStrength()));
        hardLabel.setText(String.format("%.1f", player.getHardStrength()));
    }
}