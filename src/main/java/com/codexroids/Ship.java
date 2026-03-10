package com.codexroids;

public final class Ship
{
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double angle = -90;
    private double invulnerableTimer;
    private double respawnTimer;
    private double hyperspaceCooldown;
    private boolean thrusting;
    private boolean active = true;

    public Ship(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void update(double delta)
    {
        if (invulnerableTimer > 0)
        {
            invulnerableTimer = Math.max(0, invulnerableTimer - delta);
        }
        if (respawnTimer > 0)
        {
            respawnTimer = Math.max(0, respawnTimer - delta);
        }
        if (hyperspaceCooldown > 0)
        {
            hyperspaceCooldown = Math.max(0, hyperspaceCooldown - delta);
        }
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }
    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
    public boolean isInvulnerable() { return invulnerableTimer > 0; }
    public double getInvulnerableTimer() { return invulnerableTimer; }
    public void setInvulnerableTimer(double invulnerableTimer) { this.invulnerableTimer = invulnerableTimer; }
    public double getRespawnTimer() { return respawnTimer; }
    public void setRespawnTimer(double respawnTimer) { this.respawnTimer = respawnTimer; }
    public boolean isThrusting() { return thrusting; }
    public void setThrusting(boolean thrusting) { this.thrusting = thrusting; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getHyperspaceCooldown() { return hyperspaceCooldown; }
    public void setHyperspaceCooldown(double hyperspaceCooldown) { this.hyperspaceCooldown = hyperspaceCooldown; }
}
