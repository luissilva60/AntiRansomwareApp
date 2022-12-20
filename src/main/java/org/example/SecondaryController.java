package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.io.FileInputStream;
import java.security.MessageDigest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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


    @FXML
    private Button uploadBtn;




    public void uploadImage() throws IOException, NoSuchAlgorithmException {
        String url = "http://localhost:3000/api/files";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File imageFile = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
        String path = imageFile.getAbsolutePath();
        String name = imageFile.getName();
        String hash = getHash(imageFile);
        JsonNode user = PrimaryController.response.getBody();
        int id = user.getObject().getInt("user_id");



        if (imageFile != null) {
            uploadFile(url, imageFile,name, path,hash, id);

        }


    }


    private static HttpResponse<InputStream> uploadFile(String url, File file_file, String file_name, String file_path, String file_hash, int file_user_id){
        try {


            Unirest.setTimeouts(0, 0);
            HttpResponse<InputStream> response = Unirest.post("http://localhost:3000/api/files")
                    .field("file_file", file_file)
                    .field("file_name", file_name)
                    .field("file_path", file_path)
                    .field("file_hash", file_hash)
                    .field("file_user_id", file_user_id)
                    .asBinary();

            return response;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getHash(File imgFile) throws IOException, NoSuchAlgorithmException {
        // Load the image file
        FileInputStream fis = new FileInputStream(imgFile);

// Create a MessageDigest instance
        MessageDigest md = MessageDigest.getInstance("SHA-256");

// Calculate the hash of the image
        byte[] data = new byte[1024];
        int numRead;
        while ((numRead = fis.read(data)) != -1) {
            md.update(data, 0, numRead);
        }
        byte[] hash = md.digest();

// Convert the hash to a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        String hashString = sb.toString();

// Print the hash

        return hashString;
    }



}