package application.controller;

import application.network.ThreadClient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.text.Text;


public class    ControllerScene1 {

    private Stage stage;
    private Scene scene2;
    private ThreadClient client;

    @FXML private Button _Connexion;
    @FXML private TextField tfTonPseudo;

    public ControllerScene1(Stage monStage, Scene maScene2) {
        stage = monStage;
        scene2 = maScene2;
    }

    public void setClient(ThreadClient client) {
        this.client = client;
    }

    @FXML
    public void initialize(){

        tfTonPseudo.setText("Joueur" + Math.round(Math.random() * 1000000));

        _Connexion.setOnAction(event -> {
            traiterConnexion();
        });
        tfTonPseudo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                traiterConnexion();
            }
        });
    }
    /**
     *
     * cette methode permet de traiter la connxion en vérifiant d éventuelles erreures
     * */
    private void traiterConnexion() {
        String tonPseudo = tfTonPseudo.getText();
        if (tonPseudo.trim().isEmpty()) {
            Alert dialogA = new Alert(AlertType.WARNING);
            dialogA.setTitle("Erreur");
            dialogA.setHeaderText("Pseudo incorrect");
            Text regle = new Text("Le pseudo ne doit pas être vide");
            dialogA.getDialogPane().setContent(regle);
            dialogA.showAndWait();
        }
        else {
            client.setPseudo(tonPseudo);
            client.envoyerConnexion();
        }
    }

    /**
     * llorsque le serveur a permi la connxion elle nous fait basculer vers la salle d atente avec la methode suivante
     * */
    public void afficherSalleAttente() {
        Platform.runLater(() -> {
            stage.setScene(scene2);
        });
    }


    public void afficherErreur(String str) {
        Platform.runLater(() -> {
            Alert dialogA = new Alert(AlertType.WARNING);
            dialogA.setTitle("Erreur");
            dialogA.setHeaderText("Une erreur est survenue");
            Text regle = new Text(str);
            regle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            dialogA.getDialogPane().setContent(regle);
            dialogA.showAndWait();
        });
    }
}
