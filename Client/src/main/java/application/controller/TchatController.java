package application.controller;

import application.network.ThreadClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


public class TchatController {
    @FXML private TextField _msgInput;
    @FXML private TextArea _tchat;
    @FXML private Button _senders;
    @FXML private ThreadClient client;


    public  TchatController() {}

    public void initialize() {


        _senders.setOnAction(event -> {
            if(!_msgInput.getText().isEmpty()) {
                client.envoyerMessagePublic(_msgInput.getText());
                _msgInput.clear();
            }
        });
        _msgInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !_msgInput.getText().isEmpty()) {
                client.envoyerMessagePublic(_msgInput.getText());
                _msgInput.clear();
            }
        });
    }

    public void afficherMessage(String msg){
        _tchat.appendText(msg +"\n");
    }
    public void setClient(ThreadClient client) {
        this.client = client;
    }
}
