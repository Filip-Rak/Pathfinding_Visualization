package kosmo.pathfinding;

import javafx.application.Platform;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;

public class OutputConsole
{
    // Attributes
    private TextArea outputArea;

    // Singleton
    private static OutputConsole instance;

    // Constructors
    private OutputConsole() {}

    // Methods
    public static OutputConsole get()
    {
        if (instance == null)
            instance = new OutputConsole();

        return instance;
    }

    public void writeLn(Object... args)
    {
        write(String.format("%s\n", convertArgsToString(args)));
    }

    public void write(Object... args)
    {
        if (outputArea == null)
        {
            System.err.println("Attempted to write to a null TextArea. Ensure setOutputArea has been called.");
            return;
        }

        String formattedText = convertArgsToString(args);
        Platform.runLater(() -> outputArea.appendText(formattedText));
    }

    public void writeSeparator(String separator, int length)
    {
        StringBuilder line = new StringBuilder();
        line.append(String.valueOf(separator).repeat(Math.max(0, length)));

        writeLn(line);
    }

    public void writeSeparator()
    {
        writeSeparator("-",101);
    }

    private String convertArgsToString(Object... args)
    {
        if (args.length == 0) return "";
        if (args.length == 1) return args[0].toString();

        StringBuilder sb = new StringBuilder();
        for (Object arg : args)
            sb.append(arg != null ? arg.toString() : "null");

        return sb.toString();
    }

    // Setters
    public void setOutputArea(TextArea textArea)
    {
        this.outputArea = textArea;
    }
}
