package org.example;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML
    TextField username_inp, email_inp, password_inp;

    @FXML
    private void register() throws IOException {

        registerUser(username_inp.getText(), email_inp.getText(), password_inp.getText());

        App.setRoot("primary");

        /*if(response.getStatus() == 200){
            System.out.println("ERROR");
        }else{
            System.out.println("sucess");
            App.setRoot("primary");
        }*/
    }

    private static HttpResponse<JsonNode> registerUser(String username, String email, String password){
        try {

            Unirest.setTimeouts(0, 0);
            HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.post("https://projetosd.fly.dev/api/users")
                    .field("user_name", username)
                    .field("user_email", email)
                    .field("user_password", password)
                    .asJson();
            return jsonNodeHttpResponse;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

}
