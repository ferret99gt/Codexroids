package com.codexroids;

import java.util.Random;

public final class Asteroid
{
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double rotation;
    private final double rotationSpeed;
    private final AsteroidSize size;
    private final double[] profile;

    public Asteroid(double x, double y, double vx, double vy, AsteroidSize size, Random random)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.rotation = random.nextDouble() * 360.0;
        this.rotationSpeed = -35 + random.nextDouble() * 70;
        this.profile = createProfile(random);
    }

    private double[] createProfile(Random random)
    {
        double[] data = new double[10];
        for (int i = 0; i < data.length; i++)
        {
            data[i] = 0.7 + random.nextDouble() * 0.45;
        }
        return data;
    }

    public void update(double delta)
    {
        x += vx * delta;
        y += vy * delta;
        rotation += rotationSpeed * delta;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public AsteroidSize getSize() { return size; }
    public double getRadius() { return size.getRadius(); }
    public double getRotation() { return rotation; }
    public double[] getProfile() { return profile; }
}
