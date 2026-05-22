module pl.edu.pwr.simulation.tennispropath {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens pl.edu.pwr.simulation.tennispropath to javafx.fxml;
    exports pl.edu.pwr.simulation.tennispropath;
    exports pl.edu.pwr.simulation.tennispropath.controller;
    opens pl.edu.pwr.simulation.tennispropath.controller to javafx.fxml;
}