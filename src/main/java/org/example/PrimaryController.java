package org.example;

import java.io.File;
import java.io.IOException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.json.JSONArray;

public class PrimaryController {
    HttpResponse<JsonNode> response;

    @FXML
    TextField usernameInp, passwordInp;
    @FXML
    FileChooser fileChooser = new FileChooser();

    @FXML
    private void switchToSecondary() throws IOException {



        getRandomImageAsStringFromAPI();
        response = loginUser(usernameInp.getText(), passwordInp.getText());
        if(response.getStatus() != 200){
            System.out.println("ERROR");
        }else{
            System.out.println("sucess");
            App.setRoot("secondary");
        }
        System.out.println(response.getBody().toString());


    }

    private static String getRandomImageAsStringFromAPI(){
        try {

            HttpResponse<JsonNode> apiResponse = Unirest.get("https://bandini.fly.dev/api/users").asJson();
            //FileResponse fileResponse = new Gson().fromJson(apiResponse.getBody().toString(), FileResponse.class);
            //System.out.println(apiResponse.getBody().toString());
            JSONArray results = apiResponse.getBody().getArray();
            //System.out.println(results.length());
            for(int i = 0; i < results.length(); i++){
                //System.out.println(results.get(i));
            }
            return apiResponse.getBody().toString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpResponse<JsonNode> loginUser(String email, String password){
        try {

            Unirest.setTimeouts(0, 0);
            return Unirest.post("https://projetosd.fly.dev/api/users/login")
                    .field("password", password)
                    .field("email", email)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }



}
