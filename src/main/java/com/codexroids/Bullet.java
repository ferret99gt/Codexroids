package com.codexroids;

public final class Bullet
{
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private double life = GameConfig.BULLET_LIFE;

    public Bullet(double x, double y, double vx, double vy)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
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
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}
