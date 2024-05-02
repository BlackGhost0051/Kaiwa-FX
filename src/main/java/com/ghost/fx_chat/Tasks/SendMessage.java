package com.ghost.fx_chat.Tasks;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage extends Task<String> {

    private final String message;
    private final String jwtKey;
    private final String url;

    public SendMessage(String message, String jwtKey, String url) {
        this.message = message;
        this.jwtKey = jwtKey;
        this.url = url;
    }

    @Override
    protected String call() throws Exception {
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoOutput(true);

            connection.setRequestProperty("Cookie", jwtKey);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(message.getBytes());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "Message sent successfully!";
            } else {
                return "Failed to send message. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException: " + e.getMessage();
        }
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        String result = getValue();
        System.out.println("AsyncTask Result: " + result);
        showAlert(result);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

