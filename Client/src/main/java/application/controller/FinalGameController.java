package application.controller;

import application.network.ThreadClient;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FinalGameController {

    private Stage stage;

    private Scene scene1,scene2;
    private ThreadClient client;
    private String winnerName;

    @FXML private Text _textGagnant;
    @FXML private Text _winnerName;
    @FXML Button _rejouer,_deco;
    public FinalGameController(Stage monStage,Scene maScene1 ,Scene maScene2) {
        stage = monStage;
        scene1 = maScene1;
        scene2 = maScene2;
    }

    public void setClient(ThreadClient client) {
        this.client = client;
    }

    @FXML
    public void initialize(){
        _deco.setOnAction(e -> {
            client.envoyerDeconnexion();
        });
        _rejouer.setOnAction(e -> {
           client.envoyerRejouer();
        });



    }

    public void retourFile(){
        stage.setScene(scene2);
    }


    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
        _textGagnant.setText(winnerName +" a gagné!!");
        _winnerName.setText(winnerName);
    }

}
