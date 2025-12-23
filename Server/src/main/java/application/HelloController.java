package application;

import application.network.Server;
import application.network.utils.DisplayCallBack;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextArea textAreaLogs;

    @FXML
    private Button buttonStartServer;

    private Server server;

    private Stage stage;

    @FXML
    public void initialize() {
        DisplayCallBack.setLogConsumer(msg -> {
            javafx.application.Platform.runLater(() ->
                    textAreaLogs.appendText(msg + "\n")
            );
        });
    }

    @FXML
    protected void onHelloButtonClick() {
        switchStatusServer();
        refreshMessageButtonServer();
        welcomeText.setText(messageStatusServer());
    }

    private void switchStatusServer() {
        if (server == null || !server.getStatus()) {
            try {
                server = new Server(4567);
            } catch (Exception e) {
//                textAreaLogs.appendText();
            }
            return;
        }
        server.close();
    }

    private void refreshMessageButtonServer() {
        if (server == null) return;
        if (server.getStatus()) {
            buttonStartServer.setText("Arreter le serveur");
        } else {
            buttonStartServer.setText("Lancer le serveur");
        }
    }

    private String messageStatusServer() {
        if (server == null) return null;
        System.out.println("Status du serveur : "+ server.getStatus());
        if (server.getStatus()) {
            return "Le serveur est en ligne sur le port " + server.getPort();
        } else {
            return "Le serveur est hors ligne";
        }
    }

}