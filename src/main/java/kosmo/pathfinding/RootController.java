package kosmo.pathfinding;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

public class RootController
{
    // --------------------------
    // Attributes

    // Grid
    @FXML private GridPane gridPane;
    private final GridSquare[][] displayGrid = new GridSquare[Scene.GRID_ROWS][Scene.GRID_COLUMNS];

    // Choice Boxes
    @FXML private ChoiceBox<Algorithm> algorithmChoiceBox;
    @FXML private ChoiceBox<String> sceneChoiceBox;

    // Output Console
    @FXML private TextArea consoleTextArea;

    // Vis Timer
    @FXML private Label speedLabel;

    // Scenes
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private TextField filenameField;
    @FXML private TextArea algorithmTextArea;
    private LinkedList<Scene> scenes;
    private final LinkedList<String> sceneNames = new LinkedList<>();

    // Selections   // Likely redundant - use Choice Boxes instead
    private Algorithm currentAlgorithm;
    private String currentScene;
    private boolean keyQPressed = false;
    private boolean keyEPressed = false;

    // --------------------------
    // Initialization Methods
    @FXML public void initialize()
    {
        initializeConsole();
        initializeGrid();
        initializeWand();
        initializeScenes();
        initializeChoiceBoxes();
        initializeVisTimer();
        initializeListeners();
    }

    private void initializeGrid()
    {
        // Clear the previous content
        gridPane.getChildren().clear();

        // Fill the grid with squares
        for(int row = 0; row < Scene.GRID_ROWS; row++ )
        {
            for(int col = 0; col < Scene.GRID_COLUMNS; col++)
            {
                Rectangle square = new Rectangle(Scene.SQUARE_SIZE, Scene.SQUARE_SIZE);
                gridPane.add(square, col, row);
                GridPane.setMargin(square, new Insets(1)); // Add a margin of 1 pixel

                // Add to the array
                displayGrid[row][col] = new GridSquare(square, row, col);
            }
        }

        displayGrid[0][0].setState(State.ORIGIN);
        displayGrid[Scene.GRID_ROWS - 1][Scene.GRID_COLUMNS - 1].setState(State.DESTINATION);

        // Make the squares visible
        gridPane.setGridLinesVisible(true);
    }

    private void initializeWand()
    {
        PaintWand.get().setOrigin(displayGrid[0][0]);
        PaintWand.get().setDestination(displayGrid[Scene.GRID_ROWS - 1][Scene.GRID_COLUMNS - 1]);
    }

    private void initializeScenes()
    {
        scenes = SceneLoader.loadScenes();

        // Add first scene to list as default
        scenes.addFirst(SceneLoader.createEmptyScene("default"));

        // Copy scene names
        for(Scene scene : scenes)
            sceneNames.addLast(scene.getName());

        currentScene = scenes.getFirst().getName();
        setScene(scenes.get(sceneNames.indexOf(currentScene)));

        // Set buttons
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void initializeChoiceBoxes()
    {
        // Algorithm Choice Box
        algorithmChoiceBox.getItems().addAll(Algorithm.values());
        algorithmChoiceBox.setValue(Algorithm.TEST1);
        currentAlgorithm = Algorithm.TEST1;

        // Scene Choice Box
        sceneChoiceBox.getItems().addAll(sceneNames);
        sceneChoiceBox.setValue(sceneNames.getFirst());
        currentScene = sceneNames.getFirst();
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

        // Scene Choice Box
        sceneChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::sceneChangeEvent);

        // Speed key listeners
        gridPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) // Make sure the new scene is not null
                addKeyHandlers(newScene);
        });
    }

    private void addKeyHandlers(javafx.scene.Scene scene)
    {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e ->
        {
            switch (e.getCode())
            {
                case Q:
                    keyQPressed = true;
                    break;
                case E:
                    keyEPressed = true;
                    break;
                default:
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e ->
        {
            switch (e.getCode())
            {
                case Q:
                    keyQPressed = false;
                    break;
                case E:
                    keyEPressed = false;
                    break;
                default:
            }
        });
    }

    // --------------------------
    // Event Methods

    // Simulation Running
    public void startSimulation()
    {
        Thread algorithmThread = switch (currentAlgorithm)
        {
            case TEST1 -> new Thread(new Test1Algorithm(displayGrid));
            case TEST2 -> new Thread(new Test2Algorithm(displayGrid));
        };
        
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
        double value = speedChange(Execution.get().getMaxSpeed());
        Execution.get().setSpeed(Execution.get().getSpeed() - value);
    }

    @FXML private void increaseSpeedEvent()
    {
        double value = speedChange(Execution.get().getMaxSpeed());
        Execution.get().setSpeed(Execution.get().getSpeed() + value);
    }

    private double speedChange(double extreme)
    {
        double value = 0.1;
        if (keyEPressed) value = extreme;
        else if (keyQPressed) value = 0.4;

        return value;
    }

    @FXML private void pauseButtonEvent()
    {
        if(Execution.get().isRunning())
        {
            // Toggle pause
            Execution.get().setPaused(!Execution.get().isPaused());
        }
        else if(Execution.get().isRefreshed())
        {
            Execution.get().setPaused(false);
            startSimulation();

            // Disable data buttons
            saveButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }
    }

    @FXML private void rewindEvent()
    {
        if(Execution.get().isRunning())
        {
            // Wrap up
            Execution.get().ceaseExecution();
        }
        else if(Execution.get().isRefreshed())
        {
            // Start up
            Execution.get().setPaused(false);
            startSimulation();

            // Disable data buttons
            saveButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }
        else
        {
            // Refresh
            resetAlgorithmAndScene();
        }

    }

    // Choice Boxes
    private void algorithmChangeEvent(Observable observable, Algorithm oldValue, Algorithm newValue)
    {
        if (newValue != null)
        {
            System.out.println("Algorithm changed to: " + newValue);
            currentAlgorithm = newValue;
            waitForExecutionEnd();
        }
    }

    private void sceneChangeEvent(Observable observable, String oldValue, String newValue)
    {
        if (newValue != null)
        {
            System.out.println("Scene changed to: " + newValue);
            currentScene = newValue;
            waitForExecutionEnd();
        }
    }

    private void waitForExecutionEnd()
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
            resetAlgorithmAndScene();

        }).start();
    }

    private void resetAlgorithmAndScene()
    {
        Execution.get().setRefreshed(true);
        OutputConsole.get().writeLn("Ready to reset the scene");
        setScene(scenes.get(sceneNames.indexOf(currentScene)));
    }

    private void setScene(Scene sceneToSet)
    {
        OutputConsole.get().writeLn("Setting up a scene");

        // Set buttons
        boolean isReadOnly = scenes.get(sceneNames.indexOf(currentScene)).isReadOnly();
        saveButton.setDisable(false);
        updateButton.setDisable(isReadOnly);
        deleteButton.setDisable(isReadOnly);

        // Remove previous origin from the Wand
        PaintWand.get().getOrigin().setState(State.NONE, false);
        PaintWand.get().getDestination().setState(State.NONE, false);

        GridSquare[][] gridSquares = sceneToSet.copyGrid();

        // Assign selected scene grid to displayed gird
        for(int row = 0; row < Scene.GRID_ROWS; row++)
        {
            for(int col = 0; col < Scene.GRID_COLUMNS; col++)
            {
                displayGrid[row][col].setState(gridSquares[row][col].getState(), false);

                // Update Wand references
                if(displayGrid[row][col].getState() == State.ORIGIN)
                    PaintWand.get().setOrigin(displayGrid[row][col]);

                if(displayGrid[row][col].getState() == State.DESTINATION)
                    PaintWand.get().setDestination(displayGrid[row][col]);
            }
        }

        OutputConsole.get().writeLn("Scene " + sceneToSet.getName() + " is set");
        OutputConsole.get().writeSeparator();
    }

    // Map Saving / Update / Deletion
    @FXML private void saveSceneEvent()
    {
        saveAndReloadScene(false);
        filenameField.setText("");
    }

    @FXML private void updateSceneEvent()
    {
        saveAndReloadScene(true);
    }

    @FXML private void deleteSceneEvent()
    {
        // Clear RAM
        scenes.remove(scenes.get(sceneNames.indexOf(currentScene)));
        sceneNames.remove(currentScene);
        sceneChoiceBox.getItems().remove(currentScene);

        // Delete the file
        SceneLoader.deleteScene(currentScene);

        // Load default / first scene
        sceneChoiceBox.setValue(sceneNames.getFirst());
    }

    private void saveAndReloadScene(boolean overwrite)
    {
        // Create a new one using the wand and grid in rootController
        String name = filenameField.getText();
        if(overwrite) name = currentScene;

        Scene scene = new Scene(displayGrid, name, PaintWand.get().getOrigin(), PaintWand.get().getDestination(), false);

        // This is terrible, but I spent more than three hours on trying to make it work at all
        // Scene is saved and then manually loaded again from the file
        // The scene would not set its origin and destination after saving, no matter what I did during it's entire session
        if(SceneLoader.saveScene(scene, overwrite))
        {
            try
            {
                scenes.addLast(SceneLoader.loadScene(name + "." + SceneLoader.extension));
                sceneNames.addLast(scenes.getLast().getName());
                sceneChoiceBox.getItems().addLast(sceneNames.getLast());

                // Delete previous instance
                if(overwrite)
                {
                    sceneNames.remove(currentScene);
                    scenes.remove(scenes.get(sceneNames.indexOf(currentScene)));
                    sceneChoiceBox.getItems().remove(currentScene);
                }

                sceneChoiceBox.setValue(scenes.getLast().getName());
            }
            catch (Exception e)
            {
                System.out.println("Error occurred during scene reload: " + e.getMessage());
            }
        }

        OutputConsole.get().writeSeparator();
    }
}