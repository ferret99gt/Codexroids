package com.codexroids;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class GameState
{
    public enum Status
    {
        READY,
        PLAYING,
        PAUSED,
        WAVE_CLEAR,
        GAME_OVER
    }

    private final Random random;
    private final List<Star> stars = new ArrayList<>();
    private final List<Asteroid> asteroids = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();

    private Ship ship;
    private int score;
    private int lives;
    private int wave;
    private int nextExtraLifeScore;
    private Status status = Status.READY;
    private double fireCooldown;
    private double waveClearTimer;

    public GameState(Random random)
    {
        this.random = random;
        initStars();
        startNewGame();
    }

    public void startNewGame()
    {
        score = 0;
        lives = GameConfig.STARTING_LIVES;
        wave = 1;
        nextExtraLifeScore = GameConfig.EXTRA_LIFE_SCORE;
        status = Status.READY;
        bullets.clear();
        particles.clear();
        spawnShip(true);
        spawnWave(true);
    }

    public void togglePause()
    {
        if (status == Status.PLAYING)
        {
            status = Status.PAUSED;
        }
        else if (status == Status.PAUSED)
        {
            status = Status.PLAYING;
        }
    }

    public void update(double delta, InputState input)
    {
        if (input.consumeRestart())
        {
            startNewGame();
            return;
        }

        if (input.consumePause())
        {
            togglePause();
        }

        if (status == Status.READY)
        {
            if (input.consumeStart() || input.isFire())
            {
                status = Status.PLAYING;
            }
            else
            {
                return;
            }
        }

        if (status == Status.PAUSED)
        {
            return;
        }

        updateParticles(delta);

        if (status == Status.GAME_OVER)
        {
            return;
        }

        if (status == Status.WAVE_CLEAR)
        {
            waveClearTimer -= delta;
            if (waveClearTimer <= 0)
            {
                wave += 1;
                spawnWave(false);
                status = Status.PLAYING;
            }
            return;
        }

        updateShip(delta, input);
        updateBullets(delta);
        updateAsteroids(delta);
        resolveBulletCollisions();
        resolveShipCollisions();

        if (status == Status.PLAYING && asteroids.isEmpty())
        {
            status = Status.WAVE_CLEAR;
            waveClearTimer = 1.4;
            if (ship.isActive())
            {
                ship.setInvulnerableTimer(Math.max(ship.getInvulnerableTimer(), 0.9));
            }
        }
    }

    private void initStars()
    {
        stars.clear();
        for (int i = 0; i < GameConfig.STAR_COUNT; i++)
        {
            stars.add(new Star(
                    random.nextDouble() * GameConfig.WIDTH,
                    GameConfig.HUD_HEIGHT + random.nextDouble() * (GameConfig.HEIGHT - GameConfig.HUD_HEIGHT),
                    0.8 + random.nextDouble() * 1.6));
        }
        stars.sort(Comparator.comparingDouble(Star::getRadius));
    }

    private void spawnWave(boolean resetShip)
    {
        asteroids.clear();
        bullets.clear();

        if (resetShip || ship == null)
        {
            spawnShip(true);
        }
        else if (ship.isActive())
        {
            ship.setInvulnerableTimer(Math.max(ship.getInvulnerableTimer(), 1.0));
        }

        double avoidX = ship != null && ship.isActive() ? ship.getX() : GameConfig.WIDTH * 0.5;
        double avoidY = ship != null && ship.isActive() ? ship.getY() : GameConfig.HEIGHT * 0.5;

        int largeCount = 3 + wave;
        for (int i = 0; i < largeCount; i++)
        {
            asteroids.add(createAsteroid(AsteroidSize.LARGE, avoidX, avoidY, 150));
        }
    }

    private Asteroid createAsteroid(AsteroidSize size, double avoidX, double avoidY, double avoidRadius)
    {
        double x;
        double y;
        do
        {
            x = random.nextDouble() * GameConfig.WIDTH;
            y = GameConfig.HUD_HEIGHT + random.nextDouble() * (GameConfig.HEIGHT - GameConfig.HUD_HEIGHT);
        }
        while (size == AsteroidSize.LARGE && wrappedDistance(x, y, avoidX, avoidY) < avoidRadius);

        double angle = random.nextDouble() * Math.PI * 2.0;
        double speed = switch (size)
        {
        case LARGE -> 45 + random.nextDouble() * 35 + wave * 2.5;
        case MEDIUM -> 70 + random.nextDouble() * 50 + wave * 3.0;
        case SMALL -> 95 + random.nextDouble() * 60 + wave * 3.5;
        };
        return new Asteroid(x, y, Math.cos(angle) * speed, Math.sin(angle) * speed, size, random);
    }

    private void spawnShip(boolean centered)
    {
        if (ship == null)
        {
            ship = new Ship(GameConfig.WIDTH * 0.5, GameConfig.HEIGHT * 0.5);
        }
        ship.setActive(true);
        ship.setRespawnTimer(0);
        ship.setInvulnerableTimer(GameConfig.SHIP_INVULNERABLE_TIME);
        ship.setThrusting(false);
        ship.setVx(0);
        ship.setVy(0);
        ship.setAngle(-90);
        if (centered)
        {
            ship.setX(GameConfig.WIDTH * 0.5);
            ship.setY(GameConfig.HEIGHT * 0.5);
        }
        fireCooldown = 0;
    }

    private void updateShip(double delta, InputState input)
    {
        ship.update(delta);
        if (!ship.isActive())
        {
            if (ship.getRespawnTimer() <= 0 && isSpawnAreaSafe())
            {
                spawnShip(true);
            }
            return;
        }

        if (input.isLeft())
        {
            ship.setAngle(ship.getAngle() - GameConfig.SHIP_ROTATION_SPEED * delta);
        }
        if (input.isRight())
        {
            ship.setAngle(ship.getAngle() + GameConfig.SHIP_ROTATION_SPEED * delta);
        }

        double radians = Math.toRadians(ship.getAngle());
        ship.setThrusting(input.isThrust());
        if (input.isThrust())
        {
            ship.setVx(ship.getVx() + Math.cos(radians) * GameConfig.SHIP_THRUST * delta);
            ship.setVy(ship.getVy() + Math.sin(radians) * GameConfig.SHIP_THRUST * delta);
        }

        double drag = Math.pow(GameConfig.SHIP_DRAG_PER_SECOND, delta);
        ship.setVx(ship.getVx() * drag);
        ship.setVy(ship.getVy() * drag);
        ship.setX(wrapX(ship.getX() + ship.getVx() * delta));
        ship.setY(wrapY(ship.getY() + ship.getVy() * delta));

        if (fireCooldown > 0)
        {
            fireCooldown = Math.max(0, fireCooldown - delta);
        }

        if (input.isFire() && fireCooldown <= 0)
        {
            fireCooldown = GameConfig.FIRE_COOLDOWN;
            double tipX = ship.getX() + Math.cos(radians) * (GameConfig.SHIP_RADIUS + 4);
            double tipY = ship.getY() + Math.sin(radians) * (GameConfig.SHIP_RADIUS + 4);
            bullets.add(new Bullet(
                    tipX,
                    tipY,
                    ship.getVx() + Math.cos(radians) * GameConfig.BULLET_SPEED,
                    ship.getVy() + Math.sin(radians) * GameConfig.BULLET_SPEED));
        }

        if (input.consumeHyperspace() && ship.getHyperspaceCooldown() <= 0)
        {
            ship.setX(random.nextDouble() * GameConfig.WIDTH);
            ship.setY(GameConfig.HUD_HEIGHT + random.nextDouble() * (GameConfig.HEIGHT - GameConfig.HUD_HEIGHT));
            ship.setVx(ship.getVx() * 0.45);
            ship.setVy(ship.getVy() * 0.45);
            ship.setInvulnerableTimer(1.0);
            ship.setHyperspaceCooldown(GameConfig.HYPERSPACE_COOLDOWN);
        }
    }

    private void updateBullets(double delta)
    {
        bullets.removeIf(bullet ->
        {
            bullet.update(delta);
            bullet.setX(wrapX(bullet.getX()));
            bullet.setY(wrapY(bullet.getY()));
            return bullet.isExpired();
        });
    }

    private void updateAsteroids(double delta)
    {
        for (Asteroid asteroid : asteroids)
        {
            asteroid.update(delta);
            asteroid.setX(wrapX(asteroid.getX()));
            asteroid.setY(wrapY(asteroid.getY()));
        }
    }

    private void resolveBulletCollisions()
    {
        List<Asteroid> spawned = new ArrayList<>();
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext())
        {
            Bullet bullet = bulletIterator.next();
            int hitIndex = findAsteroidHit(bullet);
            if (hitIndex < 0)
            {
                continue;
            }

            bulletIterator.remove();
            Asteroid hit = asteroids.remove(hitIndex);
            score += hit.getSize().getScore();
            awardExtraLifeIfNeeded();
            emitParticles(hit.getX(), hit.getY(), 12);

            AsteroidSize next = hit.getSize().next();
            if (next != null)
            {
                for (int i = 0; i < 2; i++)
                {
                    double angle = random.nextDouble() * Math.PI * 2.0;
                    double speed = 70 + random.nextDouble() * 60 + (next == AsteroidSize.SMALL ? 20 : 0);
                    spawned.add(new Asteroid(
                            hit.getX(),
                            hit.getY(),
                            Math.cos(angle) * speed,
                            Math.sin(angle) * speed,
                            next,
                            random));
                }
            }
        }
        asteroids.addAll(spawned);
    }

    private int findAsteroidHit(Bullet bullet)
    {
        for (int i = 0; i < asteroids.size(); i++)
        {
            Asteroid asteroid = asteroids.get(i);
            if (wrappedDistance(bullet.getX(), bullet.getY(), asteroid.getX(), asteroid.getY()) <= asteroid.getRadius())
            {
                return i;
            }
        }
        return -1;
    }

    private void resolveShipCollisions()
    {
        if (!ship.isActive() || ship.isInvulnerable())
        {
            return;
        }

        for (Asteroid asteroid : asteroids)
        {
            if (wrappedDistance(ship.getX(), ship.getY(), asteroid.getX(), asteroid.getY())
                    <= GameConfig.SHIP_RADIUS + asteroid.getRadius() * 0.82)
            {
                destroyShip();
                return;
            }
        }
    }

    private void destroyShip()
    {
        emitParticles(ship.getX(), ship.getY(), 22);
        lives -= 1;
        ship.setActive(false);
        ship.setRespawnTimer(GameConfig.SHIP_RESPAWN_DELAY);
        ship.setInvulnerableTimer(0);
        ship.setThrusting(false);
        if (lives <= 0)
        {
            status = Status.GAME_OVER;
        }
    }

    private void awardExtraLifeIfNeeded()
    {
        while (score >= nextExtraLifeScore)
        {
            lives += 1;
            nextExtraLifeScore += GameConfig.EXTRA_LIFE_SCORE;
            emitParticles(ship.getX(), ship.getY(), 10);
        }
    }

    private boolean isSpawnAreaSafe()
    {
        double spawnX = GameConfig.WIDTH * 0.5;
        double spawnY = GameConfig.HEIGHT * 0.5;
        for (Asteroid asteroid : asteroids)
        {
            if (wrappedDistance(asteroid.getX(), asteroid.getY(), spawnX, spawnY) < 130)
            {
                return false;
            }
        }
        return true;
    }

    private void emitParticles(double x, double y, int count)
    {
        for (int i = 0; i < count; i++)
        {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 35 + random.nextDouble() * 140;
            particles.add(new Particle(x, y, Math.cos(angle) * speed, Math.sin(angle) * speed, 0.45 + random.nextDouble() * 0.45));
        }
    }

    private void updateParticles(double delta)
    {
        particles.removeIf(particle ->
        {
            particle.update(delta);
            return particle.isExpired();
        });
    }

    private double wrappedDistance(double ax, double ay, double bx, double by)
    {
        double dx = wrappedDelta(ax, bx, GameConfig.WIDTH);
        double dy = wrappedDelta(ay, by, GameConfig.HEIGHT - GameConfig.HUD_HEIGHT);
        return Math.hypot(dx, dy);
    }

    private double wrappedDelta(double a, double b, double range)
    {
        double delta = a - b;
        double halfRange = range * 0.5;
        if (delta > halfRange)
        {
            delta -= range;
        }
        else if (delta < -halfRange)
        {
            delta += range;
        }
        return delta;
    }

    private double wrapX(double x)
    {
        while (x < 0)
        {
            x += GameConfig.WIDTH;
        }
        while (x > GameConfig.WIDTH)
        {
            x -= GameConfig.WIDTH;
        }
        return x;
    }

    private double wrapY(double y)
    {
        double top = GameConfig.HUD_HEIGHT;
        double range = GameConfig.HEIGHT - top;
        while (y < top)
        {
            y += range;
        }
        while (y > GameConfig.HEIGHT)
        {
            y -= range;
        }
        return y;
    }

    public List<Star> getStars()
    {
        return stars;
    }

    public List<Asteroid> getAsteroids()
    {
        return asteroids;
    }

    public List<Bullet> getBullets()
    {
        return bullets;
    }

    public List<Particle> getParticles()
    {
        return particles;
    }

    public Ship getShip()
    {
        return ship;
    }

    public int getScore()
    {
        return score;
    }

    public int getLives()
    {
        return lives;
    }

    public int getWave()
    {
        return wave;
    }

    public Status getStatus()
    {
        return status;
    }
}
