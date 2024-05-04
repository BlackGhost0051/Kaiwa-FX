package com.ghost.fx_chat;

import com.ghost.fx_chat.Interface.SSEListener;
import com.ghost.fx_chat.Model.Message;
import com.ghost.fx_chat.Tasks.SSEManager;
import com.ghost.fx_chat.Tasks.SendMessage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable, SSEListener {

    public String jwtKey = "";
    private SSEManager sseManager;

    public ChatController() {
    }

    public void setJwtKey(String jwtKeyL) {
        this.jwtKey = jwtKeyL;
        fetchChatHistory("http://localhost:8080/history", jwtKey);

        sseManager = new SSEManager("http://localhost:8080/sse", jwtKey, this );
        sseManager.connect();
    }

    @Override
    public void SSEMessage(String messageJSON) {
        System.out.println("Message JSON = " + messageJSON);
        try {
            String jsonData = messageJSON.substring(5);

            JSONObject messageObj = new JSONObject(jsonData);

            String user = messageObj.getString("user");
            String text = messageObj.getString("text");
            String date = messageObj.getString("date");

            Label messageLabel = new Label(user + ": " + text + " (" + date + ")");
            Platform.runLater(() -> messageContainer.getChildren().add(messageLabel));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendMessageBtn;

    private List<Message> messageList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList = new ArrayList<>();

        sendMessageBtn.setOnAction(event -> {
            String message = messageInput.getText().trim();
            if (!message.isEmpty()) {
                SendMessage sendMessageTask = new SendMessage(message, jwtKey, "http://localhost:8080/send");
                sendMessageTask.setOnSucceeded(e -> {
                    String result = sendMessageTask.getValue();
                    System.out.println(result);
                });
                new Thread(sendMessageTask).start();

                messageInput.clear();
            }
        });
    }

    private void fetchChatHistory(String url, String jwtToken) {
        Task<Void> fetchHistoryTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

                    connection.setRequestMethod("GET");

                    connection.setRequestProperty("Cookie", jwtToken);
                    System.out.println("JWT = " + jwtToken);
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    Platform.runLater(() -> processChatHistory(response.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(fetchHistoryTask).start();
    }



    private void processChatHistory(String historyJson) {
        try {
            JSONArray jsonArray = new JSONArray(historyJson);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject messageJson = jsonArray.getJSONObject(i);

                String user = messageJson.getString("user");
                String text = messageJson.getString("text");
                String date = messageJson.getString("date");

                Label messageLabel = new Label(user + ": " + text + " (" + date + ")");
                messageContainer.getChildren().add(messageLabel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void displayMessage(Message message) {
        Label messageLabel = new Label(message.getLogin() + ": " + message.getMessage());
        messageContainer.getChildren().add(messageLabel);
    }
}
