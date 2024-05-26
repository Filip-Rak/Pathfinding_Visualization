package kosmo.pathfinding.framework;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import kosmo.pathfinding.window.OutputConsole;
import org.kordamp.ikonli.javafx.FontIcon;

public class Execution implements Runnable
{
    // Attributes
    private final double MAX_SPEED = 5;
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
    private Label CPUTimeNano;
    private Label CPUTimeMilli;
    private Label totalTimeLabelSeconds;
    private Label pathLengthLabel;
    private Button rewindButton;
    private FontIcon syncIcon;
    int pathLength = 0;

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
        long startWait = System.nanoTime();
        long waitedTime = 0;

        try
        {
            while ((waitedTime < timeToWait || paused) && !ceaseExecution && isRunning)
            {
                Thread.sleep(10);  // Sleep for shorter intervals to remain responsive
                waitedTime = (System.nanoTime() - startWait) / 1_000_000;  // Convert nanoseconds to milliseconds

                // Break will happen if the speed is decreased far enough
                if ((!paused && (timeToWait - waitedTime < 0)) || ceaseExecution) break;
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Waiting was interrupted.");
        }

        long stopWait = System.nanoTime();
        totalWaitDuration += (stopWait - startWait);

        run();
    }

    @Override
    public void run()
    {
        if (!isRunning) {
            Platform.runLater(() -> this.rewindButton.setGraphic(syncIcon));
        }

        // Perform the time calculation
        long elapsedTimeNano = (System.nanoTime() - beginTime) - totalWaitDuration;
        long elapsedTimeMilli = elapsedTimeNano / 1_000_000;  // Convert nanoseconds to milliseconds
        long elapsedTimeTotalSeconds = (System.nanoTime() - beginTime) / 1_000_000_000;  // Convert nanoseconds to seconds

        // Pass to the method
        Platform.runLater(() -> CPUTimeNano.setText(Long.toString(elapsedTimeNano)));
        Platform.runLater(() -> CPUTimeMilli.setText(Long.toString(elapsedTimeMilli)));
        Platform.runLater(() -> totalTimeLabelSeconds.setText(Long.toString(elapsedTimeTotalSeconds)));
        Platform.runLater(() -> pathLengthLabel.setText(Integer.toString(pathLength)));
    }

    // Setters
    public void setSpeed(double speed)
    {
        double MIN_SPEED = 0.1;
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
        this.pathLength = 0;
    }

    public void setRewindButton(Button rewindButton, FontIcon icon)
    {
        this.rewindButton = rewindButton;
        this.syncIcon = icon;
    }

    public void stopPoint(int length)
    {
        this.isRunning = false;
        this.pathLength = length;

        run();

        //System.out.print("Total: " +  (System.nanoTime() - beginTime));
        //System.out.print("\tWait: " +  (totalWaitDuration));
        //System.out.print("\tCPU: " +  ((System.nanoTime() - beginTime) - (totalWaitDuration)));
        //System.out.println("\tCPU (millis): " +  ((System.nanoTime() - beginTime) - (totalWaitDuration)) / 1000000000);
    }

    public void stopPoint()
    {
        stopPoint(0);
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

    public void setLabels(Label micro, Label milli, Label total, Label pathLength)
    {
        this.CPUTimeNano = micro;
        this.CPUTimeMilli = milli;
        this.totalTimeLabelSeconds = total;
        this.pathLengthLabel = pathLength;
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

    public double getSpeed()
    {
        return this.speed;
    }

    public boolean isPaused()
    {
        return paused;
    }
}
