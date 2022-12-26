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
    requires json;
    requires com.google.api.client;
    requires org.apache.httpcomponents.httpcore;

    requires httpclient;
    requires httpmime;
    opens org.example.models.FileResponse;
    exports org.example;
    opens org.example;
}