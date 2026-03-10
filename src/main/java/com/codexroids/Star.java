package com.codexroids;

public final class Star
{
    private final double x;
    private final double y;
    private final double radius;

    public Star(double x, double y, double radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getRadius()
    {
        return radius;
    }
}
