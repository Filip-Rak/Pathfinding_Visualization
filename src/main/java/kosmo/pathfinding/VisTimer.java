package kosmo.pathfinding;

import javafx.scene.control.Label;

public class VisTimer
{
    // Attributes
    private final double MAX_SPEED = 5;
    private final double MIN_SPEED = 0.1;
    private double speed = 1;
    private final long baseDelay = 100;
    private volatile long timeToWait = baseDelay;
    private boolean paused = false;
    private Label speedLabel;

    // Singleton
    private static VisTimer instance;

    // Constructor
    private VisTimer() {}

    // Methods
    public static VisTimer getInstance()
    {
        if(instance == null)
            instance = new VisTimer();

        return instance;
    }

    public void Wait()
    {
        long startTime = System.currentTimeMillis();
        long waitedTime = 0;

        try
        {
            while (waitedTime < timeToWait || paused)
            {
                Thread.sleep(10);  // Sleep for short intervals to remain responsive
                waitedTime = System.currentTimeMillis() - startTime;

                // Break will happen if the speed is decreased far enough
                if (!paused && (timeToWait - waitedTime < 0))
                    break;
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Waiting was interrupted.");
        }
    }

    // Getters
    public double getSpeed()
    {
        return this.speed;
    }

    public boolean isPaused()
    {
        return paused;
    }

    // Setters
    public void setSpeed(double speed)
    {
        this.speed = Math.clamp(speed, MIN_SPEED, MAX_SPEED);

        timeToWait = (long)((float)(baseDelay) / this.speed);
        speedLabel.setText(String.format("%.2f", this.speed) + "x");
    }

    public void setSpeedText(Label speedLabel)
    {
        this.speedLabel = speedLabel;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }
}
