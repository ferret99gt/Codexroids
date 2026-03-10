package com.codexroids;

public final class Particle
{
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private double life;

    public Particle(double x, double y, double vx, double vy, double life)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
    }

    public void update(double delta)
    {
        x += vx * delta;
        y += vy * delta;
        life -= delta;
    }

    public boolean isExpired()
    {
        return life <= 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getLife() { return life; }
}
