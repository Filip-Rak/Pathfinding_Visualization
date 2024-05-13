package kosmo.pathfinding;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

public class RootController
{
    // --------------------------
    // Attributes

    // Grid
    @FXML private GridPane grid;
    private final int GRID_COLUMNS = 30;
    private final int GRID_ROWS = 15;
    private final double SQUARE_SIZE = 25;
    private final GridSquare[][] gridElements = new GridSquare[GRID_ROWS][GRID_COLUMNS];

    // Choice Boxes
    @FXML private ChoiceBox<Algorithm> algorithmChoiceBox;
    @FXML private ChoiceBox<MapENUM> mapChoiceBox;

    // Output Console
    @FXML private TextArea consoleTextArea;

    // Vis Timer
    @FXML private Label speedLabel;

    // Selections
    private Algorithm currentAlgorithm;
    private MapENUM currentMap;

    // --------------------------
    // Initialization Methods
    @FXML public void initialize()
    {
        initializeChoiceBoxes();
        initializeConsole();
        initializeGrid();
        initializeWand();
        initializeVisTimer();
        initializeListeners();

        // Tests
        test();
    }

    private void initializeGrid()
    {
        // Clear the previous content
        grid.getChildren().clear();

        // Fill the grid with squares
        for(int row = 0; row < GRID_ROWS; row++ )
        {
            for(int col = 0; col < GRID_COLUMNS; col++)
            {
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                grid.add(square, col, row);
                GridPane.setMargin(square, new Insets(1)); // Add a margin of 1 pixel

                // Add to the array
                gridElements[row][col] = new GridSquare(square, row, col);
            }
        }

        // Set two furthest elements as origin and destination by default
        gridElements[0][0].setState(State.ORIGIN);
        gridElements[GRID_ROWS - 1][GRID_COLUMNS - 1].setState(State.DESTINATION);

        // Make the squares visible
        grid.setGridLinesVisible(true);
    }

    private void initializeWand()
    {
        PaintWand.get().setOrigin(gridElements[0][0]);
        PaintWand.get().setDestination(gridElements[GRID_ROWS - 1][GRID_COLUMNS - 1]);
    }

    private void initializeChoiceBoxes()
    {
        // Algorithm Choice Box
        algorithmChoiceBox.getItems().addAll(Algorithm.values());
        algorithmChoiceBox.setValue(Algorithm.TEST1);
        currentAlgorithm = Algorithm.TEST1;

        // Map Choice Box
        mapChoiceBox.getItems().addAll(MapENUM.values());
        mapChoiceBox.setValue(MapENUM.TEST_MAP1);
        currentMap = MapENUM.TEST_MAP1;
    }

    private void initializeConsole()
    {
        OutputConsole.get().setOutputArea(consoleTextArea);
    }

    private void initializeVisTimer()
    {
        Execution.get().setSpeedText(speedLabel);
        Execution.get().setSpeed(1);
    }

    // Listeners
    private void initializeListeners()
    {
        // Algorithm choice box
        algorithmChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::algorithmChangeEvent);

        // Map Choice Box
        mapChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::mapChangeEvent);
    }


    // --------------------------
    // Event Methods

    // Simulation Running
    public void startSimulation()
    {
        Test1Algorithm algorithm = switch (currentAlgorithm)
        {
            case TEST1 -> new Test1Algorithm(gridElements);
            case TEST2 -> new Test1Algorithm(gridElements);
        };

        Thread algorithmThread = new Thread(algorithm);
        algorithmThread.start();
    }

    // Paint Wand
    @FXML private void originSelectedEvent()
    {
        PaintWand.get().setFunction(State.ORIGIN);
    }

    @FXML private void destinationSelectedEvent()
    {
        PaintWand.get().setFunction(State.DESTINATION);
    }

    @FXML private void obstacleSelectedEvent()
    {
        PaintWand.get().setFunction(State.OBSTACLE);
    }

    // Simulation Control
    @FXML private void decreaseSpeedEvent()
    {
        Execution.get().setSpeed(Execution.get().getSpeed() - 0.1);
    }

    @FXML private void increaseSpeedEvent()
    {
        Execution.get().setSpeed(Execution.get().getSpeed() + 0.1);
    }

    @FXML private void togglePauseEvent()
    {
        if(Execution.get().isRunning())
            Execution.get().setPaused(!Execution.get().isPaused());
    }

    @FXML private void rewindEvent()
    {
        if(Execution.get().isRunning())
        {
            Execution.get().ceaseExecution();
        }
        else
        {
            Execution.get().setPaused(false);
            startSimulation();
        }

    }

    // Choice Boxes
    private void algorithmChangeEvent(Observable observable, Algorithm oldValue, Algorithm newValue)
    {
        if (newValue != null)
        {
            System.out.println("Algorithm changed to: " + newValue);
            currentAlgorithm = newValue;
            waitForExecution();
        }
    }

    private void mapChangeEvent(Observable observable, MapENUM oldValue, MapENUM newValue)
    {
        if (newValue != null)
        {
            System.out.println("Map changed to: " + newValue);
            currentMap = newValue;
            waitForExecution();
        }
    }

    private void waitForExecution()
    {
        Execution.get().ceaseExecution();

        new Thread(() ->
        {
            while (Execution.get().isRunning())
            {
                try { Thread.sleep(100);}
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            resetAlgorithmAndMap();

        }).start();
    }

    private void resetAlgorithmAndMap()
    {
        OutputConsole.get().writeLn("READY");
    }

    // -------------------------
    // Testing Methods
    private void test()
    {
        LinkedList<String> filenames = MapLoader.getFileNames();
    }
}