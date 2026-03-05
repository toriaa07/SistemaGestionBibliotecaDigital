module com.bibliotec {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires okhttp3;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.bibliotec to javafx.fxml;
    opens com.bibliotec.controller to javafx.fxml;
    opens com.bibliotec.model to com.fasterxml.jackson.databind;

    exports com.bibliotec;
    exports com.bibliotec.controller;
    exports com.bibliotec.model;
    exports com.bibliotec.service;
    exports com.bibliotec.util;
}
