package kosmo.pathfinding;

import javafx.scene.shape.Rectangle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;

public class SceneLoader
{
    // Attributes
    private static final String directoryPath = "scenes";
    public static final String extension = "pfscene";

    // Methods
    public static LinkedList<String> getFileNames()
    {
        File directory = new File(directoryPath);
        FilenameFilter filter = (dir, name) -> name.endsWith("." + extension);
        File[] files = directory.listFiles(filter);

        LinkedList<String> names = new LinkedList<>();
        if(files != null)
        {
            for(File file: files)
                names.add(file.getName());
        }


        // Console print
        OutputConsole.get().writeLn("Looking for scene files at: " + directory.getAbsolutePath());

        OutputConsole.get().write("Found scenes: ");
        for(int i = 0; i < names.size() - 1; i++)
            OutputConsole.get().write(names.get(i) + ", ");

        OutputConsole.get().writeLn(names.getLast());

        return  names;
    }

    public static LinkedList<Scene> loadScenes()
    {
        OutputConsole.get().writeSeparator();
        LinkedList<String> filenames = getFileNames();

        LinkedList<Scene> scenes = new LinkedList<>();
        for(String name : filenames)
        {
            try
            {
                scenes.add(loadScene(name));
            }
            catch (Exception e)
            {
                OutputConsole.get().writeLn(e);
            }
        }

        OutputConsole.get().writeSeparator();

        return scenes;
    }

    public static Scene loadScene(String sceneName) throws Exception
    {
        String filename = directoryPath + "/" + sceneName;

        GridSquare[][] gridElements = new GridSquare[Scene.GRID_ROWS][Scene.GRID_COLUMNS];
        GridSquare origin = null, destination = null;
        boolean readOnly = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line;
            for(int i = -2; i < Scene.GRID_ROWS; i++)
            {
                line = reader.readLine();

                if(i == -1) readOnly = line.startsWith("*");
                if(i < 0) continue;

                // Split the line into columns based on tabs
                String[] columns = line.split(" {2}");    // 3 spaces because that's how a 'tab' looks like in vs code

                // Skip the first column and process the rest
                for (int j = 1; j < Scene.GRID_COLUMNS + 1; j++)
                {
                    Rectangle square = new Rectangle(Scene.SQUARE_SIZE, Scene.SQUARE_SIZE);
                    gridElements[i][j-1] = new GridSquare(square, i, j - 1);

                    switch (columns[j].trim())
                    {
                        case "o":
                        case "O":
                            if(origin != null) throw new Exception("Scene " + filename + " is invalid. Multiple origins");
                            gridElements[i][j-1].setState(State.ORIGIN, false);
                            origin = gridElements[i][j-1];
                            break;
                        case "d":
                        case "D":
                            if(destination != null) throw new Exception("Scene " + filename + " is invalid. Multiple destinations");
                            gridElements[i][j-1].setState(State.DESTINATION, false);
                            destination = gridElements[i][j-1];
                            break;

                        case "x":
                        case "X":
                            gridElements[i][j-1].setState(State.OBSTACLE, false);
                            break;

                        default:
                            gridElements[i][j-1].setState(State.NONE, false);
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new Exception(e.getMessage());
        }

        if(origin != null && destination != null)
        {
            String name = filename.substring(directoryPath.length() + 1, filename.length() - SceneLoader.extension.length() - 1);
            return new Scene(gridElements, name, origin, destination, readOnly);
        }
        else
            throw new Exception("Scene " + filename + " is invalid. No origin or destination");
    }

    public static Scene createEmptyScene(String name)
    {
        GridSquare[][] grid = new GridSquare[Scene.GRID_ROWS][Scene.GRID_COLUMNS];

        for(int i = 0; i < Scene.GRID_ROWS; i++)
        {
            for(int j = 0; j < Scene.GRID_COLUMNS; j++)
            {
                Rectangle square = new Rectangle(Scene.SQUARE_SIZE, Scene.SQUARE_SIZE);
                grid[i][j] = new GridSquare(square, i, j);
            }
        }

        // Set two furthest elements as origin and destination
        grid[0][0].setState(State.ORIGIN);
        grid[Scene.GRID_ROWS - 1][Scene.GRID_COLUMNS - 1].setState(State.DESTINATION);

        return new Scene(grid, name, grid[0][0], grid[Scene.GRID_ROWS - 1][Scene.GRID_COLUMNS - 1], true);
    }

    public static boolean saveScene(Scene scene, boolean overwrite)
    {
        if(overwrite)
            OutputConsole.get().writeLn("Updating scene: " + scene.getName() + "." + extension);
        else
            OutputConsole.get().writeLn("Attempting to save the scene as: " + scene.getName() + "." + extension);

        if(Objects.equals(scene.getName(), "custom") || Objects.equals(scene.getName(), "default") || Objects.equals(scene.getName(), ""))
        {
            OutputConsole.get().writeLn("Stopping. Name invalid, change name");
            return false;
        }

        File file = new File( directoryPath + "/" + scene.getName() + "." + extension);

        // Check if the scene already exists
        if (!file.exists() || overwrite)
        {
            try (FileWriter writer = new FileWriter(file))
            {
                String header = "o = origin; d = destination; x = obstacle; y = none; * in col 2 row 1 means it's read only; default.pfscene and custom.pfscene are reserved\n" +
                        "-   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18  19  20  21  22  23  24  25  26  27  28  29  30";

                writer.write(header);

                GridSquare[][] gridSquares = scene.copyGrid();
                for(int i = 0; i < Scene.GRID_ROWS; i++)
                {
                    writer.write("\n" + (i + 1));
                    for(int j = 0; j < Scene.GRID_COLUMNS; j++)
                    {
                        String character = switch (gridSquares[i][j].getState())
                        {
                            case ORIGIN -> "o";
                            case DESTINATION -> "d";
                            case OBSTACLE -> "x";
                            default -> "y";
                        };

                        writer.write("   " + character);
                    }
                }

                OutputConsole.get().writeLn("Scene saved");
                return true;
            }
            catch (IOException e)
            {
                OutputConsole.get().writeLn("Error writing to file: " + e.getMessage());
                return false;
            }
        }
        else
        {
            OutputConsole.get().writeLn("Scene of this name already exists");
            return false;
        }
    }

    public static void deleteScene(String name)
    {
        String filePath = directoryPath + "/" + name + "." + extension;
        Path path = Paths.get(filePath);

        try
        {
            // Attempt to delete the file
            Files.delete(path);
            System.out.println("Scene: " + name + " has been deleted");
        }
        catch (IOException e)
        {
            System.out.println("Failed to delete the file: " + e.getMessage());
        }
    }
}
