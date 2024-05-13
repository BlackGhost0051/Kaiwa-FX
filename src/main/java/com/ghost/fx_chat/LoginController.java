package com.ghost.fx_chat;

import com.ghost.fx_chat.Tasks.LoginTask;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import okhttp3.*;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;


public class LoginController {
    private String URL = "http://localhost:8080";
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    public Button goRegister;

    @FXML
    public void initialize() {
        goRegister.setOnMouseClicked(event -> {
            Scene scene = goRegister.getScene();
            Stage stage = (Stage) scene.getWindow();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
                Scene registerScene = new Scene(root);
                registerScene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setTitle("Register");
                stage.setScene(registerScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                LoginButtonClick();
            }
        });
    }

    @FXML
    public void LoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        LoginTask loginTask = new LoginTask(username, password, URL);

        loginTask.setOnSucceeded(event -> {
            String jwtKey = loginTask.getValue();
            System.out.println(jwtKey);
            if (jwtKey != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat.fxml"));
                    Parent root = fxmlLoader.load();

                    ChatController chatController = fxmlLoader.getController();
                    chatController.setJwtKey(jwtKey);

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    Scene chatScene = new Scene(root);
                    chatScene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                    stage.setScene(chatScene);
                    stage.setTitle("Chat");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Error login!");
                System.out.println("Error login!");
            }
        });
        new Thread(loginTask).start();
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
