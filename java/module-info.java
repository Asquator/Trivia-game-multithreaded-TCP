module com.example.trivia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.trivia to javafx.fxml;
    exports com.example.trivia;
}