module torneo.proyectotorneo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires static lombok;
    requires java.sql;
 

    opens torneo.proyectotorneo to javafx.fxml;
    exports torneo.proyectotorneo;
    exports torneo.proyectotorneo.viewController;
    opens torneo.proyectotorneo.viewController to javafx.fxml;
    opens torneo.proyectotorneo.model to javafx.base, javafx.fxml;
    opens torneo.proyectotorneo.model.enums to javafx.base, javafx.fxml;
    exports torneo.proyectotorneo.model;
    exports torneo.proyectotorneo.model.enums;
}