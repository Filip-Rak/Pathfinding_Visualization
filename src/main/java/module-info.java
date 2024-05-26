module kosmo.pathfinding
{
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.fontawesome5;

    opens kosmo.pathfinding to javafx.fxml;
    exports kosmo.pathfinding;
    exports kosmo.pathfinding.algorithm;
    opens kosmo.pathfinding.algorithm to javafx.fxml;
    exports kosmo.pathfinding.framework;
    opens kosmo.pathfinding.framework to javafx.fxml;
    exports kosmo.pathfinding.window;
    opens kosmo.pathfinding.window to javafx.fxml;
}