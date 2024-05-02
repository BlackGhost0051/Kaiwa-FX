module com.ghost.fx_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires okhttp3;
    requires org.json;

    opens com.ghost.fx_chat to javafx.fxml;
    exports com.ghost.fx_chat;
}