module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    //needed for HTTP
    requires unirest.java;

    //needed for JSON
    requires gson;

    //needed for JavaFX
    //needed for JSON

    requires java.sql;
    opens org.example.models.FileResponse to gson;
    opens org.example to javafx.fxml;
    exports org.example;
}