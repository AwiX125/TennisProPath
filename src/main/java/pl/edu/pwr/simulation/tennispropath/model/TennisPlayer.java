package pl.edu.pwr.simulation.tennispropath.model;

public abstract class TennisPlayer {
    protected String name;
    protected int skillLevel;

    public TennisPlayer(String name, int skillLevel) {
        this.name = name;
        this.skillLevel = skillLevel;
    }

    public String getName() {
        return name;
    }
    public int getSkillLevel() {
        return skillLevel;
    }
}