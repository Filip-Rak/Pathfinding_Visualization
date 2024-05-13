package kosmo.pathfinding;

import javafx.scene.shape.Rectangle;

import java.io.*;
import java.util.LinkedList;

public class SceneLoader
{
    // Attributes
    private static final String path = "scenes";
    private static final String extension = "pfscene";

    // Methods
    public static LinkedList<String> getFileNames()
    {
        File directory = new File(path);
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
                scenes.add(loadScene(path +"/" + name));
            }
            catch (Exception e)
            {
                OutputConsole.get().writeLn(e);
            }
        }

        OutputConsole.get().writeSeparator();

        return scenes;
    }

    private static Scene loadScene(String filename) throws Exception
    {
        GridSquare[][] gridElements = new GridSquare[Scene.GRID_ROWS][Scene.GRID_COLUMNS];
        GridSquare origin = null, destination = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line;
            for(int i = -2; i < Scene.GRID_ROWS; i++)
            {
                line = reader.readLine();

                if(i < 0)
                    continue;

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
            String name = filename.substring(path.length() + 1, filename.length() - SceneLoader.extension.length() - 1);
            return new Scene(gridElements, name, origin, destination);
        }
        else
            throw new Exception("Scene " + filename + " is invalid. No origin or destination");
    }
}
