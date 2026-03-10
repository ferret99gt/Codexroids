package com.codexroids;

public enum AsteroidSize
{
    LARGE(GameConfig.LARGE_ASTEROID_RADIUS, 20),
    MEDIUM(GameConfig.MEDIUM_ASTEROID_RADIUS, 50),
    SMALL(GameConfig.SMALL_ASTEROID_RADIUS, 100);

    private final double radius;
    private final int score;

    AsteroidSize(double radius, int score)
    {
        this.radius = radius;
        this.score = score;
    }

    public double getRadius()
    {
        return radius;
    }

    public int getScore()
    {
        return score;
    }

    public AsteroidSize next()
    {
        return switch (this)
        {
        case LARGE -> MEDIUM;
        case MEDIUM -> SMALL;
        case SMALL -> null;
        };
    }
}
