package com.ghost.fx_chat.Tasks;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class LoginTask extends Task<String> {

    private final String username;
    private final String password;
    private final String urlStr;

    public LoginTask(String username, String password, String urlStr) {
        this.username = username;
        this.password = password;
        this.urlStr = urlStr;
    }

    @Override
    protected String call() throws Exception {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "username=" + username + "&password=" + password;
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookies = headerFields.get("Set-Cookie");
                List<String> location = headerFields.get("Location");

                if (location != null) {
                    for (String loc : location) {
                        if (loc.contains("?error")) {
                            return null;
                        }
                    }
                }

                if (cookies != null) {
                    String cookie = cookies.get(0);
                    String jwtKey = "";
                    for (int i = 0; i < cookie.length(); i++) {
                        char currentChar = cookie.charAt(i);
                        if (currentChar == ';') {
                            return jwtKey;
                        }
                        jwtKey += currentChar;
                    }
                    return null;
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

