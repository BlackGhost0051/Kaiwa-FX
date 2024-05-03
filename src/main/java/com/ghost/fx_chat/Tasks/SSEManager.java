package com.ghost.fx_chat.Tasks;


import com.ghost.fx_chat.Interface.SSEListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SSEManager {

    private final String sseUrl;
    private String jwtKey;
    private boolean isRunning;
    private Thread sseThread;

    private SSEListener listener;

    public SSEManager(String sseUrl, String jwtKey, SSEListener listener) {
        this.sseUrl = sseUrl;
        this.jwtKey = jwtKey;
        this.listener = listener;
        this.isRunning = false;

        System.out.println("SSE jwtKey = " + jwtKey);
    }

    public void connect() {
        if (!isRunning) {
            isRunning = true;
            sseThread = new Thread(new SSETask());
            sseThread.start();
        }
    }

    public void disconnect() {
        isRunning = false;
    }

    private class SSETask implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL(sseUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setRequestProperty("Cookie", jwtKey);

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while (isRunning) {
                    String line = reader.readLine();
                    if (line == null) {
                        // Connection was closed
                        break;
                    }

                    processSSEEvent(line);
                }

                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processSSEEvent(String eventData) {
        if (!eventData.isEmpty()) {
            listener.SSEMessage(eventData);

        }
    }
}
