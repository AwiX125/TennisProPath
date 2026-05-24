package pl.edu.pwr.simulation.tennispropath.model;

public class Opponent extends TennisPlayer {
    public Opponent(String name, int skillLevel, int age, double forehandStrength, double backhandStrength, double serveStrength, double grassStrength, double clayStrength, double hardStrength) {
        super(name, skillLevel, age);
        this.forehandStrength = forehandStrength;
        this.backhandStrength = backhandStrength;
        this.serveStrength = serveStrength;
        this.grassStrength = grassStrength;
        this.clayStrength = clayStrength;
        this.hardStrength = hardStrength;
    }
}