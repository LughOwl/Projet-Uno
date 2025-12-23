package application;

import application.controller.*;

import application.network.ThreadClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class Main extends Application {

    private Scene scene1;
    private Scene scene2;
    private Scene scene3;
    private Scene scene4;
    private Scene popupTchat;

    private ControllerScene1 controleur1;
    private ControllerScene2 controleur2;
    private GameController controleur3;
    private FinalGameController controleur4;
    private TchatController controleurTchat;

    private ThreadClient client;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.scene1 = new Scene(new GridPane(), 750, 750);
            this.scene2 = new Scene(new GridPane(), 750, 750);
            this.scene3 = new Scene(new GridPane(), 810, 850);
            this.scene4 = new Scene(new GridPane(), 750, 750);
            this.popupTchat = new Scene(new GridPane());

            controleur1 = new ControllerScene1(primaryStage,scene2);
            controleur2 = new ControllerScene2(primaryStage,scene1,scene3);
            controleur3 = new GameController(primaryStage,scene4,popupTchat);
            controleur4 = new FinalGameController(primaryStage,scene1,scene2);
            controleurTchat = new TchatController();

            FXMLLoader loaderS1 = new FXMLLoader(getClass().getResource("/application/view/Scene1.fxml"));
            loaderS1.setController(controleur1);
            this.scene1.setRoot(loaderS1.load());

            FXMLLoader loaderS2 = new FXMLLoader(getClass().getResource("/application/view/Scene2.fxml"));
            loaderS2.setController(controleur2);
            this.scene2.setRoot(loaderS2.load());

            FXMLLoader loaderS3 = new FXMLLoader(getClass().getResource("/application/view/GameView.fxml"));
            loaderS3.setController(controleur3);
            this.scene3.setRoot(loaderS3.load());

            FXMLLoader loaderS4 = new FXMLLoader(getClass().getResource("/application/view/FinalGame.fxml"));
            loaderS4.setController(controleur4);
            this.scene4.setRoot(loaderS4.load());

            FXMLLoader loaderTchat = new FXMLLoader(getClass().getResource("/application/view/TchatDialogView.fxml"));
            loaderTchat.setController(controleurTchat);
            this.popupTchat.setRoot(loaderTchat.load());



            client = new ThreadClient(controleur1, controleur2, controleur3, controleur4,controleurTchat);
            controleur1.setClient(client);
            controleur2.setClient(client);
            controleur3.setClient(client);
            controleur4.setClient(client);
            controleurTchat.setClient(client);

            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            primaryStage.setScene(scene1);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}