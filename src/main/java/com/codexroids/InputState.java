package com.codexroids;

public final class InputState
{
    private boolean left;
    private boolean right;
    private boolean thrust;
    private boolean fire;
    private boolean pausePressed;
    private boolean restartPressed;
    private boolean startPressed;
    private boolean hyperspacePressed;

    public boolean isLeft()
    {
        return left;
    }

    public void setLeft(boolean left)
    {
        this.left = left;
    }

    public boolean isRight()
    {
        return right;
    }

    public void setRight(boolean right)
    {
        this.right = right;
    }

    public boolean isThrust()
    {
        return thrust;
    }

    public void setThrust(boolean thrust)
    {
        this.thrust = thrust;
    }

    public boolean isFire()
    {
        return fire;
    }

    public void setFire(boolean fire)
    {
        this.fire = fire;
    }

    public void requestPause()
    {
        pausePressed = true;
    }

    public boolean consumePause()
    {
        boolean value = pausePressed;
        pausePressed = false;
        return value;
    }

    public void requestRestart()
    {
        restartPressed = true;
    }

    public boolean consumeRestart()
    {
        boolean value = restartPressed;
        restartPressed = false;
        return value;
    }

    public void requestStart()
    {
        startPressed = true;
    }

    public boolean consumeStart()
    {
        boolean value = startPressed;
        startPressed = false;
        return value;
    }

    public void requestHyperspace()
    {
        hyperspacePressed = true;
    }

    public boolean consumeHyperspace()
    {
        boolean value = hyperspacePressed;
        hyperspacePressed = false;
        return value;
    }
}
