// RegisterController.java
package com.ghost.fx_chat;

import com.ghost.fx_chat.Tasks.RegisterTask;
import com.ghost.fx_chat.Interface.RegisterTaskListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController implements RegisterTaskListener {

    private String URL = "http://localhost:8080/register";
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    public Label goLogin;


    @FXML
    public void initialize() {
        goLogin.setOnMouseClicked(event -> {
            Scene scene = goLogin.getScene();
            Stage stage = (Stage) scene.getWindow();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Scene registerScene = new Scene(root);
                stage.setTitle("Login");
                stage.setScene(registerScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void RegisterButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        RegisterTask registerTask = new RegisterTask(username, password, URL, this);
        Thread thread = new Thread(registerTask);
        thread.start();
    }

    @Override
    public void onTaskComplete(int code) {
        if (code == 200) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed with response code: " + code);
        }
    }
}
