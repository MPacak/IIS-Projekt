module hr.iisclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires java.validation;
    opens hr.iisclient to javafx.fxml;
    opens hr.iisclient.controller to javafx.fxml;
    exports hr.iisclient;
}