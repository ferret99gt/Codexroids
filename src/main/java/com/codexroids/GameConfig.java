package com.codexroids;

public final class GameConfig
{
    public static final int WIDTH = 960;
    public static final int HEIGHT = 720;
    public static final int HUD_HEIGHT = 72;
    public static final int STAR_COUNT = 120;

    public static final double SHIP_RADIUS = 14;
    public static final double SHIP_ROTATION_SPEED = 210;
    public static final double SHIP_THRUST = 220;
    public static final double SHIP_DRAG_PER_SECOND = 0.92;
    public static final double SHIP_RESPAWN_DELAY = 1.1;
    public static final double SHIP_INVULNERABLE_TIME = 2.0;
    public static final double HYPERSPACE_COOLDOWN = 2.0;

    public static final double BULLET_SPEED = 430;
    public static final double BULLET_LIFE = 1.2;
    public static final double FIRE_COOLDOWN = 0.18;

    public static final double LARGE_ASTEROID_RADIUS = 54;
    public static final double MEDIUM_ASTEROID_RADIUS = 34;
    public static final double SMALL_ASTEROID_RADIUS = 20;

    public static final int STARTING_LIVES = 3;

    public static final int SCORE_LARGE = 20;
    public static final int SCORE_MEDIUM = 50;
    public static final int SCORE_SMALL = 100;
    public static final int EXTRA_LIFE_SCORE = 10_000;

    private GameConfig()
    {
    }
}
