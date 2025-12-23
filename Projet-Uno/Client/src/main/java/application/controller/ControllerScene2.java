package application.controller;

import application.network.ThreadClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ControllerScene2 {

    private Stage stage;
    private Scene scene1;
    private Scene scene3;
    private ThreadClient client;

    @FXML private TextArea tchat;
    @FXML private TextField tfMessage;

    @FXML private Label lblNbJoueurs;
    @FXML private Label lblPseudo1;
    @FXML private Label lblPseudo2;
    @FXML private Label lblPseudo3;
    @FXML private Label lblPseudo4;
    private Label[] pseudos;

    @FXML private Circle pastillePseudo1;
    @FXML private Circle pastillePseudo2;
    @FXML private Circle pastillePseudo3;
    @FXML private Circle pastillePseudo4;
    private Circle[] pastilles;

    @FXML private Button _Pret;
    @FXML private Button _LancerLaPartie;
    @FXML private Button _SeDeconnecter;
    @FXML private Button _EnvoyerMessage;

    public ControllerScene2(Stage monStage, Scene maScene1, Scene maScene3) {
        stage = monStage;
        scene1 = maScene1;
        scene3 = maScene3;
    }

    public void setClient(ThreadClient client) {
        this.client = client;
    }

    @FXML
    public void initialize(){
        pseudos = new Label[] {lblPseudo1, lblPseudo2, lblPseudo3, lblPseudo4};
        pastilles = new Circle[] {pastillePseudo1, pastillePseudo2, pastillePseudo3, pastillePseudo4};

        _Pret.setOnAction(e -> {
            client.envoyerPret();
        });
        _LancerLaPartie.setOnAction(event -> {
            traiterLancerLaPartie();
        });
        _SeDeconnecter.setOnAction(event -> {
            client.envoyerDeconnexion();
        });
        _EnvoyerMessage.setOnAction(event -> {
            if(!tfMessage.getText().isEmpty()){
                client.envoyerMessagePublic(tfMessage.getText());
                tfMessage.clear();
            }
        });
        tfMessage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !tfMessage.getText().isEmpty()) {
                client.envoyerMessagePublic(tfMessage.getText());
                tfMessage.clear();
            }
        });
    }

    public void afficherListeJoueur(String str) {
        Platform.runLater(() -> {
            String[] infos = str.split(" ");

            int joueurCount = 0;
            for (int i = 0; i < 4 && i < infos.length; i++) {
                String full = infos[i];
                // Trouve l'index du crochet ouvrant
                int idx = full.indexOf('[');
                String pseudo = full.substring(0, idx);
                boolean estPret = Boolean.parseBoolean(full.substring(idx + 1, full.length() - 1));

                pseudos[i].setText(pseudo);
                pastilles[i].setFill(estPret ? Color.LIMEGREEN : Color.RED);
                joueurCount++;
            }

            // Efface les emplacements non utilisés
            for (int i = joueurCount; i < 4; i++) {
                pseudos[i].setText("");
                pastilles[i].setFill(Color.GRAY);
            }

            lblNbJoueurs.setText("Joueurs connectés : " + joueurCount + "/4");
        });
    }

    public void afficherJoueurPret(String joueur) {
        Platform.runLater(() -> {
            for (int i = 0; i < pseudos.length; i++) {
                if (pseudos[i].getText().equals(joueur)) {
                    pastilles[i].setFill(Color.LIMEGREEN);
                    break;
                }
            }
            if (client.getPseudo().equals(joueur)) {
                _Pret.setText("Annuler");
                _Pret.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
            }
        });
    }

    public void afficherJoueurPasPret(String joueur) {
        Platform.runLater(() -> {
            for (int i = 0; i < pseudos.length; i++) {
                if (pseudos[i].getText().equals(joueur)) {
                    pastilles[i].setFill(Color.RED);
                    break;
                }
            }
            if (client.getPseudo().equals(joueur)) {
                _Pret.setText("Prêt");
                _Pret.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");
            }
        });
    }

    public void afficherDeconnexion(String str){
        Platform.runLater(() -> {
            if (client.getPseudo().equals(str)) {
                client.setEstPret(false);
                _Pret.setText("Prêt");
                _Pret.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");
                stage.setScene(scene1); // retour à l'écran d'accueil
            } else {
                int index = -1;
                // Trouver le joueur à supprimer
                for (int i = 0; i < pseudos.length; i++) {
                    if (pseudos[i].getText().equals(str)) {
                        index = i;
                        break;
                    }
                }

                // Si trouvé, décaler les suivants
                if (index != -1) {
                    for (int i = index; i < pseudos.length - 1; i++) {
                        pseudos[i].setText(pseudos[i + 1].getText());
                        pastilles[i].setFill(pastilles[i + 1].getFill());
                    }

                    // Nettoyer la dernière case
                    pseudos[pseudos.length - 1].setText("");
                    pastilles[pastilles.length - 1].setFill(Color.GRAY);
                }

                // Recalculer le nombre de joueurs affichés
                int joueursConnectés = 0;
                for (Label pseudo : pseudos) {
                    if (!pseudo.getText().isEmpty()) {
                        joueursConnectés++;
                    }
                }
                lblNbJoueurs.setText("Joueurs connectés : " + joueursConnectés + "/4");
            }
        });
    }

    public void afficherMessage(String str) {
        tchat.appendText(str + "\n");
    }

    private void traiterLancerLaPartie() {
        client.envoyerLancerPartie();
    }

    public void afficherPartie(){
        stage.setScene(scene3);
    }
}
