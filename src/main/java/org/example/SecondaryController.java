package org.example;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.example.models.FileResponse.FileResponse;
import org.json.JSONArray;
import java.io.StringReader;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class SecondaryController implements Initializable {
    private Timeline timeline;


    @FXML
    private TableView<FileResponse> fileTable;
    @FXML
    private TableColumn<FileResponse, String> nameCol, fileCol, pathCol, hashCol;
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Retrieve the data for the table
        HttpResponse<String> response = null;
        JsonNode user = PrimaryController.response.getBody();
        int userId = user.getObject().getInt("user_id");
        try {
            response = Unirest.get("https://projetosd.fly.dev/api/files/user/"+ userId).asString();
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
        stopBackup();
    }


    @FXML
    private Button uploadBtn;




    public void uploadBtnEvent() throws IOException, NoSuchAlgorithmException, UnirestException {
        String url = "https://projetosd.fly.dev/api/files";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File imageFile = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
        String path = imageFile.getAbsolutePath().toString();
        String name = imageFile.getName().toString();
        String hash = getHash(imageFile);
        JsonNode user = PrimaryController.response.getBody();
        int id = user.getObject().getInt("user_id");
        String image_link = imageUpload(imageFile);
        System.out.println(image_link);




        if (imageFile != null) {
            filePostRequest(url, image_link,name, path,hash, id);
            //HttpResponse<JsonNode> response = uploadFile(url, imageFile,name, path,hash, id);

            //System.out.println(response.getStatus());
        }


    }


    private static void filePostRequest(String url, String file_link, String file_name, String file_path, String file_hash, int file_user_id) throws IOException, UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response =  Unirest.post(url)
                .field("file_file", file_link)
                .field("file_name", file_name)
                .field("file_path", file_path)
                .field("file_hash", file_hash)
                .field("file_user_id", file_user_id)
                .asString();
        int status = response.getStatus();
        System.out.println("Status: " + status);

    }
    public static String getHash(File imgFile) throws IOException, NoSuchAlgorithmException {
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
    public static String imageUpload(File file){
        try {
            Unirest.setTimeouts(0, 0);
            // Create a POST request to the server with the image data
            HttpResponse<String> response = Unirest.post("https://projetosd.fly.dev/api/files/upload")
                    .field("file_file", file)
                    .asString();

            // Check the response status and handle it appropriately
            System.out.println(response.getBody().toString());
            if (response.getStatus() == 200) {
                System.out.println("Image uploaded successfully");
                System.out.println(response.getBody().toString());

                return response.getBody().toString();
            } else {
                System.out.println("Error uploading image: " + response.getStatusText());
            }
        } catch (UnirestException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public  void backup(){
        // Create a timeline that will run the updateTimer function every minute
        HttpResponse<String> response = null;
        JsonNode user = PrimaryController.response.getBody();
        int userId = user.getObject().getInt("user_id");
        try {
            response = Unirest.get("https://projetosd.fly.dev/api/files/user/"+ userId).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String array = response.getBody();

        ObservableList<FileResponse> items = FXCollections.observableArrayList();
        // (Parsing the JSON array using Gson)
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<FileResponse>>(){}.getType();
        List<FileResponse> itemList = gson.fromJson(array, listType);
        items.addAll(itemList);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            try {
                updateTimer(items);
            } catch (NoSuchAlgorithmException | IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Run indefinitely
        timeline.play(); // Start the timeline
    }

    private static void updateTimer(ObservableList<FileResponse> items) throws IOException, NoSuchAlgorithmException {
        // This function will be called every minute
        System.out.println("One minute has passed");
        for (FileResponse file:
                items) {
            String path = file.getFile_path();
            String hash = file.getFile_hash();
            String foto = file.getFile_file();

            File fis = new File(path);
            String hashNew = getHash(fis);
            if(hash.equals(hashNew)){
                System.out.println("File secure");
            }else {
                System.out.println("File has been altered");
                File newFile = new File(foto);
                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(Files.readAllBytes(newFile.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }







        }


    }

    public void stopBackup() {
        // Stop the timeline
        timeline.stop();
    }


    public void refreshTable(){
        // Retrieve the data for the table
        HttpResponse<String> response = null;
        JsonNode user = PrimaryController.response.getBody();
        int userId = user.getObject().getInt("user_id");
        try {
            response = Unirest.get("https://projetosd.fly.dev/api/files/user/"+ userId).asString();
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

}