package org.example;

import java.io.IOException;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import org.example.models.FileResponse.FileResponse;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {


        App.setRoot("secondary");
        getRandomImageAsStringFromAPI();


    }

    private static String getRandomImageAsStringFromAPI(){
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("https://bandini.fly.dev/api/products/60").asJson();
            FileResponse fileResponse = new Gson().fromJson(apiResponse.getBody().toString(), FileResponse.class);
            System.out.println(fileResponse.getProduct_id());
            return fileResponse.getProduct_photo();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

}
