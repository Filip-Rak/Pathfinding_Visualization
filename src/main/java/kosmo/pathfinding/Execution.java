package kosmo.pathfinding;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

public class Execution implements Runnable
{
    // Attributes
    private final double MAX_SPEED = 5;
    private final double MIN_SPEED = 0.1;
    private double speed = 1;
    private final long BASE_DELAY = 100;
    private volatile long timeToWait = BASE_DELAY;

    // Duration measuring
    private long beginTime = System.nanoTime();
    private long totalWaitDuration = 0;

    // Flags
    private boolean paused = true;
    private boolean ceaseExecution = true;
    private boolean isRunning = false;
    private boolean isRefreshed = true;

    // External elements
    private Label speedLabel;
    private Label CPUTimeMicro;
    private Label CPUTimeMilli;
    private Label totalTimeLabelSeconds;
    private Button rewindButton;
    private FontIcon syncIcon;

    // Singleton
    private static Execution instance;

    // Constructor
    private Execution() {}

    // Methods
    public static Execution get()
    {
        if(instance == null)
            instance = new Execution();

        return instance;
    }

    public void Wait()
    {
        long startTime = System.currentTimeMillis();
        long startTimeNano = System.nanoTime();
        long waitedTime = 0;

        try
        {
            while ((waitedTime < timeToWait || paused) && !ceaseExecution && isRunning)
            {
                Thread.sleep(10);  // Sleep for short intervals to remain responsive
                waitedTime = System.currentTimeMillis() - startTime;

                // Break will happen if the speed is decreased far enough
                if ((!paused && (timeToWait - waitedTime < 0)) || ceaseExecution) break;
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Waiting was interrupted.");
        }

        totalWaitDuration += (System.nanoTime() - startTimeNano);

        run();
    }

    @Override
    public void run()
    {
        if(!isRunning) Platform.runLater(() -> this.rewindButton.setGraphic(syncIcon));

        // Perform the time calculation
        long elapsedTimeMicro = ((System.nanoTime() - beginTime) - totalWaitDuration) / 1000;
        long elapsedTimeMilli = ((System.nanoTime() - beginTime) - totalWaitDuration) / 1000000000;
        long elapsedTimeTotalSeconds = ((System.nanoTime() - beginTime)) / 1000000000;

        // Pass to the method
        Platform.runLater(()-> CPUTimeMicro.setText(Long.toString(elapsedTimeMicro)));
        Platform.runLater(()-> CPUTimeMilli.setText(Long.toString(elapsedTimeMilli)));
        Platform.runLater(()-> totalTimeLabelSeconds.setText(Long.toString(elapsedTimeTotalSeconds)));
    }

    // Setters
    public void setSpeed(double speed)
    {
        this.speed = Math.clamp(speed, MIN_SPEED, MAX_SPEED);

        timeToWait = (long)((float)(BASE_DELAY) / this.speed);
        speedLabel.setText(String.format("%.2f", this.speed) + "x");
    }

    public void ceaseExecution()
    {
        if(!isRunning)
            return;

        this.ceaseExecution = true;
        Platform.runLater(() -> OutputConsole.get().writeLn("WRAPPING UP THE EXECUTION"));
    }

    public void startPoint()
    {
        this.ceaseExecution = false;
        this.isRunning = true;
        this.isRefreshed = false;

        this.totalWaitDuration = 0;
        this.beginTime = System.nanoTime();
    }

    public void setRewindButton(Button rewindButton, FontIcon icon)
    {
        this.rewindButton = rewindButton;
        this.syncIcon = icon;
    }

    public void stopPoint()
    {
        this.isRunning = false;
        run();

        System.out.print("Total: " +  (System.nanoTime() - beginTime));
        System.out.print("\tWait: " +  (totalWaitDuration));
        System.out.print("\tCPU: " +  ((System.nanoTime() - beginTime) - (totalWaitDuration)));
        System.out.println("\tCPU (millis): " +  ((System.nanoTime() - beginTime) - (totalWaitDuration)) / 1000000000);
    }

    public void setSpeedText(Label speedLabel)
    {
        this.speedLabel = speedLabel;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    public void setRefreshed(boolean refreshed)
    {
        isRefreshed = refreshed;
    }

    public void setCPUTimeLabels(Label micro, Label milli, Label total)
    {
        this.CPUTimeMicro = micro;
        this.CPUTimeMilli = milli;
        this.totalTimeLabelSeconds = total;
    }

    // Getters
    public boolean isRunning()
    {
        return this.isRunning;
    }

    public boolean isRefreshed()
    {
        return isRefreshed;
    }

    public double getMaxSpeed()
    {
        return MAX_SPEED;
    }

    public double getMinSpeed()
    {
        return MIN_SPEED;
    }

    public double getSpeed()
    {
        return this.speed;
    }

    public boolean isPaused()
    {
        return paused;
    }
}
