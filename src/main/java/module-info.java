module torneo.proyectotorneo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires static lombok;
    requires java.sql;
    requires java.desktop;

    // ⭐ AGREGAR ESTAS LÍNEAS PARA JASPER
    requires jasperreports;
    requires java.xml;
    requires javafx.swing;  // ⭐ IMPORTANTE: Permite usar SwingNode

    opens torneo.proyectotorneo to javafx.fxml;
    exports torneo.proyectotorneo;
    exports torneo.proyectotorneo.viewController;
    opens torneo.proyectotorneo.viewController to javafx.fxml;
    opens torneo.proyectotorneo.model to javafx.base, javafx.fxml;
    opens torneo.proyectotorneo.model.enums to javafx.base, javafx.fxml;
    exports torneo.proyectotorneo.model;
    exports torneo.proyectotorneo.model.enums;

    // ⭐ AGREGAR ESTAS LÍNEAS
    exports torneo.proyectotorneo.controller;
    opens torneo.proyectotorneo.controller to javafx.fxml;
}