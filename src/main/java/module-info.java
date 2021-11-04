module com.syntechpro.dataproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires com.google.gson;
    requires jdk.httpserver;

    opens com.syntechpro.dataproject to javafx.fxml;
    exports com.syntechpro.dataproject;
    exports com.syntechpro.dataproject.JsonClass;
    exports com.syntechpro.dataproject.Controller;
    exports com.syntechpro.dataproject.Utils;
    opens com.syntechpro.dataproject.Controller to javafx.fxml;
    exports com.syntechpro.dataproject.Server;
    opens com.syntechpro.dataproject.Server to javafx.fxml;
}