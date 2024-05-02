package com.ghost.fx_chat.Tasks;

import com.ghost.fx_chat.Interface.RegisterTaskListener;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterTask extends Task<Integer> {

    private final String registerUrl;
    private final String name;
    private final String password;
    private final RegisterTaskListener listener;

    public RegisterTask(String name, String password, String registerUrl, RegisterTaskListener listener) {
        this.registerUrl = registerUrl;
        this.name = name;
        this.password = password;
        this.listener = listener;
    }

    @Override
    protected Integer call() {
        try {
            URL url = new URL(registerUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"name\":\"" + name + "\", \"password\":\"" + password + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        if (getValue() != -1) {
            listener.onTaskComplete(getValue());
        } else {
            System.err.println("Error during registration");
        }
    }
}

