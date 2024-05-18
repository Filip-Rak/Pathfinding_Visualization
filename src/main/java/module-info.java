module kosmo.pathfinding {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.fontawesome5;

    opens kosmo.pathfinding to javafx.fxml;
    exports kosmo.pathfinding;
}