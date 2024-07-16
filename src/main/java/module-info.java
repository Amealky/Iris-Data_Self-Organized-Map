module bm.classification.som.irisdatasom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires commons.math3;
    requires com.fasterxml.jackson.databind;

    opens bm.classification.som.irisdatasom to javafx.fxml;
    exports bm.classification.som.irisdatasom;
}