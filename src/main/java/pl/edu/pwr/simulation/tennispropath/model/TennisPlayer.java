package pl.edu.pwr.simulation.tennispropath.model;

public abstract class TennisPlayer {
    // Atrybuty tenisowe (1 - 100)
    protected double forehandStrength;
    protected double backhandStrength;
    protected double serveStrength;

    // Atrybuty nawierzchni (1 - 100)
    protected double clayStrength;
    protected double grassStrength;
    protected double hardStrength;

    // Profil
    protected String name;
    protected int age;
    protected int skillLevel;


    public TennisPlayer(String name, int skillLevel, int age) {
        this.name = name;
        this.age = age;
        this.skillLevel = skillLevel;
    }



    // GETTERY I SETTERY

    // Atrybuty tenisowe i kortu
    public double getForehandStrength() { return forehandStrength; }
    public double getBackhandStrength() { return backhandStrength; }
    public double getServeStrength() { return serveStrength; }
    public double getClayStrength() { return clayStrength; }
    public double getGrassStrength() { return grassStrength; }
    public double getHardStrength() { return hardStrength; }

    // Profil
    public String getName() {return name;}
    public int getSkillLevel() {return skillLevel;}
    public int getAge() { return age; }
}