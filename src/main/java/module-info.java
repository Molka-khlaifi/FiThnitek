module com.example.couvoiturage {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.mail;
    requires jbcrypt;

    opens com.example.couvoiturage to javafx.fxml;
    opens com.example.couvoiturage.controller to javafx.fxml;
    opens com.example.couvoiturage.model to javafx.base, javafx.fxml;
    exports com.example.couvoiturage;
    exports com.example.couvoiturage.controller;
    exports com.example.couvoiturage.model;
}