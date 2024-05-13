package kosmo.pathfinding;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

public class MapLoader
{
    // Attributes
    private static final String path = "maps";
    private static final String extension = "pfmap";

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
        OutputConsole.get().writeSeparator();
        OutputConsole.get().writeLn("Looking for map files at: " + directory.getAbsolutePath());

        OutputConsole.get().write("Found maps: ");
        for(int i = 0; i < names.size() - 1; i++)
            OutputConsole.get().write(names.get(i) + ", ");

        OutputConsole.get().writeLn(names.getLast());
        OutputConsole.get().writeSeparator();

        return  names;
    }


}
