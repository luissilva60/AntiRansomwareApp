package org.example;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import org.example.models.FileResponse.Decryptor;
import org.example.models.FileResponse.FileResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.example.models.FileResponse.Decryptor.decrypt;
import static org.example.models.FileResponse.Decryptor.decryptAes256Gcm;


public class SecondaryController implements Initializable {
    private Timeline timeline;

    private JsonNode data = PrimaryController.response.getBody();

    private JSONObject user = (JSONObject) data.getObject().get("user");


    private static final String token = PrimaryController.response.getBody().getObject().getString("token");
    @FXML
    private TableView<FileResponse> fileTable;
    @FXML
    private TableColumn<FileResponse, String> nameCol, fileCol, pathCol;

    @FXML private TableColumn<FileResponse, Button> hashCol;

   /* public void initialize(URL url, ResourceBundle resourceBundle) {
        // Retrieve the data for the table

        JsonNode data = PrimaryController.response.getBody();
        String userData = "";
        String decrypted = "";
        String encrypted = data.getObject().getString("user");
        System.out.println(encrypted);
        try {
            decrypted = decrypt(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("swwwwwwwww"+ decrypted);
        Gson userdata = new Gson();
        String s = userdata.toJson(decrypted);
        System.out.println(s);
        JSONObject userd = new JSONObject(decrypted);
        System.out.println(userd);
        HttpResponse<String> response = null;
        JsonNode user = PrimaryController.response.getBody();
        int userId = userd.getInt("user_id");
        try {
            response = Unirest.get("https://projetosd.herokuapp.com/api/files/user/" + userId)
                    .header("x-access-token", token).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        if (response != null) {
            System.out.println(userd);
            String jsonArray = response.getBody();
            ObservableList<FileResponse> items = FXCollections.observableArrayList();
            // (Parsing the JSON array using Gson)
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<FileResponse>>() {}.getType();
            List<FileResponse> itemList = gson.fromJson(jsonArray, listType);
            items.addAll(itemList);

            // Set the items and cell value factories for the table and columns
            fileTable.setItems(items);
            nameCol.setCellValueFactory(new PropertyValueFactory<>("file_name"));
            fileCol.setCellValueFactory(new PropertyValueFactory<>("file_file"));
            pathCol.setCellValueFactory(new PropertyValueFactory<>("file_path"));
            hashCol.setCellValueFactory(new PropertyValueFactory<>("file_hash"));
        }
    }*/
   public void initialize(URL url, ResourceBundle resourceBundle) {
       // Retrieve the data for the table

       JsonNode data = PrimaryController.response.getBody();
       //String encrypted = data.getObject().getString("user");





       HttpResponse<String> response = null;

       JSONObject user = (JSONObject) data.getObject().get("user");
       int userId = user.getInt("user_id");
       try {
           response = Unirest.get("https://projetosd.herokuapp.com/api/files/user/" + userId)
                   .header("x-access-token", token).asString();
       } catch (UnirestException e) {
           e.printStackTrace();
       }

       if (response != null) {

           String jsonArray = response.getBody();
           ObservableList<FileResponse> items = FXCollections.observableArrayList();
           // (Parsing the JSON array using Gson)
           Gson gson = new Gson();
           Type listType = new TypeToken<ArrayList<FileResponse>>() {}.getType();
           List<FileResponse> itemList = gson.fromJson(jsonArray, listType);
           items.addAll(itemList);

           // Set the items and cell value factories for the table and columns
           fileTable.setItems(items);
           nameCol.setCellValueFactory(new PropertyValueFactory<>("file_name"));
           fileCol.setCellValueFactory(new PropertyValueFactory<>("file_file"));
           pathCol.setCellValueFactory(new PropertyValueFactory<>("file_path"));
           hashCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FileResponse, Button>, ObservableValue<Button>>() {
               @Override
               public ObservableValue<Button> call(TableColumn.CellDataFeatures<FileResponse, Button> features) {
                   FileResponse fileResponse = features.getValue();

                   Button button = new Button("Delete");
                   button.setOnAction(new EventHandler<ActionEvent>() {
                       @Override
                       public void handle(ActionEvent event) {
                           // This is the function that will be called when the button is clicked
                           try {
                               buttonEvent(fileResponse);
                           } catch (IOException e) {
                               e.printStackTrace();
                           } catch (UnirestException e) {
                               e.printStackTrace();
                           }
                       }
                   });

                   return new SimpleObjectProperty<>(button);
               }
           });
       }
   }
   public void buttonEvent(FileResponse fileResponse) throws IOException, UnirestException {
       System.out.println("yooooooo");


       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Confirmation Dialog");
       alert.setHeaderText(null);
       alert.setContentText("Are you sure you want to do this?");

       ButtonType buttonYes = new ButtonType("Yes");
       ButtonType buttonNo = new ButtonType("No");
       ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
       alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

       Optional<ButtonType> result = alert.showAndWait();
       if (result.get() == buttonYes) {
           // User clicked "Yes" button
           // Perform some action
           System.out.println("You clicked yes");
           fileDeleteRequest(fileResponse.getFile_id());
       } else if (result.get() == buttonNo) {
           // User clicked "No" button
           // Perform some action
           System.out.println("You clicked No");
       } else {
           // User clicked "Cancel" or closed the dialog
           // Perform some action
           System.out.println("You clicked cancel");
       }
   }

    private static void fileDeleteRequest(int id) throws IOException, UnirestException {
        Unirest.setTimeouts(0, 0);

        HttpResponse<String> response =  Unirest.delete("https://projetosd.herokuapp.com/api/files/"+ id)
                .header("x-access-token", token)
                .asString();
        int status = response.getStatus();
        System.out.println("Status: " + status);

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
        stopBackup();
    }


    @FXML
    private Button uploadBtn;




    public void uploadBtnEvent() throws IOException, NoSuchAlgorithmException, UnirestException {
        String url = "https://projetosd.herokuapp.com/api/files";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File imageFile = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
        String path = imageFile.getAbsolutePath().toString();
        String name = imageFile.getName().toString();
        String hash = getHash(imageFile);

        int id = user.getInt("user_id");
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
                .header("x-access-token", token)
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
            HttpResponse<String> response = Unirest.post("https://projetosd.herokuapp.com/api/files/upload")
                    .header("x-access-token", token)
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
        int userId = user.getInt("user_id");


        try {
            response = Unirest.get("https://projetosd.herokuapp.com/api/files/user/"+ userId)
                    .header("x-access-token", token)
                    .asString();
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
            } catch (NoSuchAlgorithmException | IOException | URISyntaxException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Run indefinitely
        timeline.play(); // Start the timeline
    }
    @FXML private static Label secondary;

    private static void updateTimer(ObservableList<FileResponse> items) throws IOException, NoSuchAlgorithmException, URISyntaxException {
        // This function will be called every minute
        System.out.println("One minute has passed");
        for (FileResponse file:
                items) {
            String path = file.getFile_path();
            String hash = file.getFile_hash();
            String foto = file.getFile_file();

            File fis = new File(path);
            if(fis.exists()){
                String hashNew = getHash(fis);
                if(hash.equals(hashNew)){
                    System.out.println("File secure");
                }else {
                    System.out.println("File has been altered");
                    URI imageUri = new URI(foto);
                    try (InputStream in = imageUri.toURL().openStream()) {
                        Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Alerta de Deteção de Atividade Anómala");
                        alert.setHeaderText(null);
                        alert.setContentText(file.getFile_name() + " has been altered. Backing-up to pc.");
                        secondary.setText(file.getFile_name() + " has been altered. Backing-up to pc.");
                        secondary.setTextFill(Color.RED);

                    } catch (IOException e) {
                        // Handle exception
                        e.printStackTrace();
                    }
                }
            }else {
                System.out.println("File has been altered");
                URI imageUri = new URI(foto);
                try (InputStream in = imageUri.toURL().openStream()) {
                    Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Back-up done!");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alerta de Deteção de Atividade Anómala");
                    alert.setHeaderText(null);
                    alert.setContentText(file.getFile_name() + " has been altered. Backing-up to pc.");

                    
                } catch (IOException e) {
                    // Handle exception
                    e.printStackTrace();
                }
                /*try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(bufferedImage, "jpeg", new File(path));
                } catch (IOException e) {
                    // Handle exception
                }*/
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

        int userId = user.getInt("user_id");
        try {
            response = Unirest.get("https://projetosd.herokuapp.com/api/files/user/"+ userId)
                    .header("x-access-token", token)
                .asString();
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
        hashCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FileResponse, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<FileResponse, Button> features) {
                FileResponse fileResponse = features.getValue();

                Button button = new Button("Click Me");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // This is the function that will be called when the button is clicked
                        try {
                            buttonEvent(fileResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (UnirestException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return new SimpleObjectProperty<>(button);
            }
        });

    }





}