package com.ghost.fx_chat.Model;

public class Message {

    private String login;
    private String message;
    private String data;


    public Message(String login, String message, String data) {
        this.login = login;
        this.message = message;
        this.data = data;
    }

    public boolean hasImage() {
        return message != null && message.toLowerCase().endsWith(".png") || message != null && message.toLowerCase().endsWith(".jpg");
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

