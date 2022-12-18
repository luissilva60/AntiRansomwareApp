package org.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.FileResponse.FileResponse;

public class SecondaryController implements Initializable {
    @FXML
    private TableView<FileResponse> fileTable;
    @FXML
    private TableColumn<FileResponse, String> nameCol, fileCol, pathCol, hashCol;
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Retrieve the data for the table
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://projetosd.fly.dev/api/files").asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        String jsonArray = response.getBody();
        ObservableList<FileResponse> items = FXCollections.observableArrayList();
        // (Parsing the JSON array using Gson)
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<FileResponse>>(){}.getType();
        List<FileResponse> itemList = gson.fromJson(jsonArray, listType);
        items.addAll(itemList);

        // Set the items and cell value factories for the table and columns
        fileTable.setItems(items);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("file_name"));
        fileCol.setCellValueFactory(new PropertyValueFactory<>("file_file"));
        pathCol.setCellValueFactory(new PropertyValueFactory<>("file_path"));
        hashCol.setCellValueFactory(new PropertyValueFactory<>("file_hash"));


    }


    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }


}