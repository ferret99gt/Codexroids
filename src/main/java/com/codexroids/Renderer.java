package com.codexroids;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public final class Renderer
{
    private final Font hudFont = Font.font("Consolas", FontWeight.BOLD, 20);
    private final Font titleFont = Font.font("Consolas", FontWeight.EXTRA_BOLD, 38);
    private final Font infoFont = Font.font("Consolas", FontWeight.SEMI_BOLD, 18);

    public void render(GraphicsContext gc, GameState state)
    {
        drawBackground(gc, state);
        drawHud(gc, state);
        drawParticles(gc, state);
        drawAsteroids(gc, state);
        drawBullets(gc, state);
        drawShip(gc, state.getShip());
        drawOverlay(gc, state);
    }

    private void drawBackground(GraphicsContext gc, GameState state)
    {
        LinearGradient sky = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(7, 12, 28)),
                new Stop(1, Color.rgb(2, 6, 16)));
        gc.setFill(sky);
        gc.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        for (Star star : state.getStars())
        {
            gc.setFill(Color.color(0.88, 0.94, 1.0, 0.65));
            gc.fillOval(star.getX(), star.getY(), star.getRadius(), star.getRadius());
        }
    }

    private void drawHud(GraphicsContext gc, GameState state)
    {
        gc.setFill(Color.rgb(10, 16, 30, 0.92));
        gc.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HUD_HEIGHT);

        gc.setFont(hudFont);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.rgb(140, 225, 255));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score " + state.getScore(), 18, 24);
        gc.fillText("Lives " + state.getLives(), 18, 50);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Wave " + state.getWave(), GameConfig.WIDTH / 2.0, 24);
        gc.setFont(infoFont);
        gc.fillText("Turn  |  Thrust  |  Fire  |  H hyperspace", GameConfig.WIDTH / 2.0, 50);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("P pause  |  R restart", GameConfig.WIDTH - 18, 24);
    }

    private void drawParticles(GraphicsContext gc, GameState state)
    {
        for (Particle particle : state.getParticles())
        {
            gc.setFill(Color.color(1.0, 0.78, 0.38, Math.min(1.0, particle.getLife() * 1.4)));
            gc.fillOval(particle.getX(), particle.getY(), 3, 3);
        }
    }

    private void drawAsteroids(GraphicsContext gc, GameState state)
    {
        gc.setStroke(Color.rgb(200, 215, 235));
        gc.setLineWidth(2);
        for (Asteroid asteroid : state.getAsteroids())
        {
            double[] xs = new double[asteroid.getProfile().length];
            double[] ys = new double[asteroid.getProfile().length];
            for (int i = 0; i < asteroid.getProfile().length; i++)
            {
                double angle = Math.toRadians(asteroid.getRotation() + i * (360.0 / asteroid.getProfile().length));
                double radius = asteroid.getRadius() * asteroid.getProfile()[i];
                xs[i] = asteroid.getX() + Math.cos(angle) * radius;
                ys[i] = asteroid.getY() + Math.sin(angle) * radius;
            }
            gc.strokePolygon(xs, ys, xs.length);
        }
    }

    private void drawBullets(GraphicsContext gc, GameState state)
    {
        gc.setFill(Color.rgb(255, 235, 160));
        for (Bullet bullet : state.getBullets())
        {
            gc.fillOval(bullet.getX() - 2, bullet.getY() - 2, 4, 4);
        }
    }

    private void drawShip(GraphicsContext gc, Ship ship)
    {
        if (ship == null || !ship.isActive())
        {
            return;
        }

        if (ship.isInvulnerable() && ((int) (ship.getInvulnerableTimer() * 12) % 2 == 0))
        {
            return;
        }

        double radians = Math.toRadians(ship.getAngle());
        double left = Math.toRadians(ship.getAngle() + 135);
        double right = Math.toRadians(ship.getAngle() - 135);

        double noseX = ship.getX() + Math.cos(radians) * (GameConfig.SHIP_RADIUS + 4);
        double noseY = ship.getY() + Math.sin(radians) * (GameConfig.SHIP_RADIUS + 4);
        double leftX = ship.getX() + Math.cos(left) * GameConfig.SHIP_RADIUS;
        double leftY = ship.getY() + Math.sin(left) * GameConfig.SHIP_RADIUS;
        double rightX = ship.getX() + Math.cos(right) * GameConfig.SHIP_RADIUS;
        double rightY = ship.getY() + Math.sin(right) * GameConfig.SHIP_RADIUS;

        gc.setStroke(ship.isInvulnerable() ? Color.rgb(160, 235, 255) : Color.WHITE);
        gc.setLineWidth(2);
        gc.strokePolygon(
                new double[] { noseX, leftX, rightX },
                new double[] { noseY, leftY, rightY },
                3);

        if (ship.isThrusting())
        {
            double flameX = ship.getX() - Math.cos(radians) * (GameConfig.SHIP_RADIUS + 8);
            double flameY = ship.getY() - Math.sin(radians) * (GameConfig.SHIP_RADIUS + 8);
            gc.setStroke(Color.rgb(255, 175, 90));
            gc.strokeLine((leftX + rightX) * 0.5, (leftY + rightY) * 0.5, flameX, flameY);
        }
    }

    private void drawOverlay(GraphicsContext gc, GameState state)
    {
        if (state.getStatus() == GameState.Status.PLAYING)
        {
            return;
        }

        gc.setFill(Color.rgb(5, 10, 18, 0.64));
        gc.fillRect(0, GameConfig.HUD_HEIGHT, GameConfig.WIDTH, GameConfig.HEIGHT - GameConfig.HUD_HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (state.getStatus() == GameState.Status.READY)
        {
            gc.setFill(Color.rgb(150, 225, 255));
            gc.setFont(titleFont);
            gc.fillText("CODEXROIDS", GameConfig.WIDTH / 2.0, 290);
            gc.setFont(infoFont);
            gc.fillText("Survive the asteroid field", GameConfig.WIDTH / 2.0, 334);
            gc.fillText("Press Enter or Space to start", GameConfig.WIDTH / 2.0, 366);
        }
        else if (state.getStatus() == GameState.Status.PAUSED)
        {
            gc.setFill(Color.rgb(255, 225, 150));
            gc.setFont(titleFont);
            gc.fillText("PAUSED", GameConfig.WIDTH / 2.0, 316);
            gc.setFont(infoFont);
            gc.fillText("Press P to resume", GameConfig.WIDTH / 2.0, 356);
        }
        else if (state.getStatus() == GameState.Status.WAVE_CLEAR)
        {
            gc.setFill(Color.rgb(165, 245, 180));
            gc.setFont(titleFont);
            gc.fillText("FIELD CLEAR", GameConfig.WIDTH / 2.0, 316);
            gc.setFont(infoFont);
            gc.fillText("Next wave incoming", GameConfig.WIDTH / 2.0, 356);
        }
        else if (state.getStatus() == GameState.Status.GAME_OVER)
        {
            gc.setFill(Color.rgb(255, 150, 120));
            gc.setFont(titleFont);
            gc.fillText("GAME OVER", GameConfig.WIDTH / 2.0, 306);
            gc.setFont(infoFont);
            gc.fillText("Press R to restart", GameConfig.WIDTH / 2.0, 348);
        }
    }
}
