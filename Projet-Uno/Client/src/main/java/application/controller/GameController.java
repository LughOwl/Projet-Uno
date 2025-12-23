package application.controller;


import application.network.ThreadClient;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static application.utils.UtilsCardDisplay.pathToText;
import static application.utils.UtilsCardDisplay.textToPath;

public class GameController {

    private Stage stage;
    private Stage chatStage = new Stage();
    private Scene scene4, sceneTchat;
    private ThreadClient client;
    private boolean chatIsOpen = false,isChoosing = false;
    private HashMap<String, Text> playersPlace = new HashMap<>();
    private HashMap<String,Text> namePlace = new HashMap<>();
    private HashMap<String,ImageView> imageCardPlace = new HashMap<>();
    @FXML private Text _clientPseudo, _currentPlayer,_namePlayer3,_namePlayer2,_namePlayer4,_colorChoisi;
    @FXML private HBox _mainJoueur1,_mainJoueur1_v2;
    @FXML private Text _mainJoueur3;
    @FXML private Text _mainPlayer2;
    @FXML private Text _mainJoueur4;
    @FXML private Pane _choiceColor;
    @FXML private ImageView _zoneCarteJouee,_zoneUnoValidationJ1;
    @FXML private Button _deconnecter,_buttonUno,_openChat,_passeToure,_btnEncaisser;
    @FXML private Button _pioche;
    @FXML private ImageView _zoneJ2,_zoneJ3,_zoneJ4;
    @FXML private Rectangle _rouge;
    @FXML private Rectangle _bleu;
    @FXML private Rectangle _jaune;
    @FXML private Rectangle _vert ;

    /*
    * constructeur
    * */
    public GameController(Stage monStage,Scene scene,Scene sceneTchat) {
        stage = monStage;
        scene4 = scene;
        this.sceneTchat = sceneTchat;
    }



    public void setClient(ThreadClient client) {
        this.client = client;
    }




    public void initialize() {
        _deconnecter.setOnAction(event -> {
            client.envoyerDeconnexion();
        });

        _buttonUno.setOnAction(event -> {
            client.envoyerUno();
        });

        chatStage.setOnCloseRequest(event -> {chatIsOpen = false;});
        _openChat.setOnAction(event -> {
            if(!chatIsOpen){
                chatStage.show();
                chatIsOpen = true;
            }
        });
        _btnEncaisser.setOnAction(event -> {
           client.envoyerEncaisser();
        });

        _passeToure.setOnAction(event -> {
            client.envoyerPasseTour();
        });
        _pioche.setOnAction(event -> {
            client.envoyerPioche();
        });


        chatStage.initOwner(stage);
        chatStage.setResizable(false);
        chatStage.setScene(sceneTchat);

        _zoneJ2.setImage(null);
        _zoneJ3.setImage(null);
        _zoneJ4.setImage(null);

        _colorChoisi.setText(null);
        _zoneCarteJouee.setImage(null);
        _zoneUnoValidationJ1.setImage(null);

        //_zoneEncaisser.getChildren().clear();
        _mainPlayer2.setText(null);
        _mainJoueur4.setText(null);
        _mainJoueur3.setText(null);
        _choiceColor.setVisible(false);
        _namePlayer2.setText(null);
        _namePlayer4.setText(null);
        _namePlayer3.setText(null);

    }


    /**
    *methode pour enlever le logo uno a coter su nom du joueur
    * */
    public void enleverUno(){
        _zoneUnoValidationJ1.setImage(null);

    }

    /**
    * methode pour afficher nos cartes et d affecter a un evement qui fait aappelle a la methode actioncCard()
    * */

    public void constructHand(String[] mots) {
        _mainJoueur1.getChildren().clear();
        _mainJoueur1_v2.getChildren().clear();

        _clientPseudo.setText(client.getPseudo());

        for (int i=1; i<mots.length; i++) {

            String [] motSplit = mots[i].split(",");
            ImageView img;

            try {
                 img = new ImageView(new Image(Objects.requireNonNull(GameController.class.getResource(textToPath(motSplit))).toExternalForm() ));
                //System.out.println("img -> text : "+ UtilsCardDisplay.dysplayToText(img.getImage()));
                img.setFitHeight(71);
                img.setFitWidth(51);

                if(_mainJoueur1.getChildren().size() >= 16) {
                    _mainJoueur1_v2.getChildren().add(img);
                }else{_mainJoueur1.getChildren().add(img);}

            }catch (Exception e) {System.out.println("error img path");}


        }
        for (var card: _mainJoueur1.getChildren()) {
            card.setOnMouseClicked(event -> {
                ImageView imgV = (ImageView) event.getTarget();
                if(!isChoosing){actionCard(imgV.getImage());}
            });
        }
        for (var card: _mainJoueur1_v2.getChildren()) {
            card.setOnMouseClicked(event -> {
                ImageView imgV = (ImageView) event.getTarget();
                if(!isChoosing){actionCard(imgV.getImage());}
            });
        }

    }

    /**
    * evenement lorsqu on clique sur une carte, elle enleve la carte de la main et la met dans le tas/sabot
    * @param img image de la carte
    * */
    private void actionCard(Image img ){

        switch (pathToText(img)){
            case "WILD_DRAW_FOUR"->{
                try {
                    ouvrirrChoixCouleur("WILD_DRAW_FOUR");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "WILD" -> {
                try {
                    ouvrirrChoixCouleur("WILD");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> {
                client.envoyerVerifCard(pathToText(img));
                _colorChoisi.setText(null);
            }
        }
    }
    /**
    * @param  card , carte a ajouter
    * cette fonction est apellé lorsqu un joueur a choisi une carte pur le jouer
    * */
    public void ajoutSabot(String[] card){
        Image img = new Image(Objects.requireNonNull(GameController.class.getResource(textToPath(card))).toExternalForm());
        _zoneCarteJouee.setImage(img);
    }
    /**
    * @param mots , liste des joueures
    * permet de placer les advairsaires à leurs places respectifs selon la vu du joueur
    * */
    public void placePlayersName(String[] mots) {
        Image dosCarte = new Image(Objects.requireNonNull(GameController.class.getResource("/images/ariere_carte.png")).toExternalForm());
        if(mots.length ==3){
            for(int i =1;i<mots.length;i++ ) {
                if( mots[i].equals(client.getPseudo())){
                    if(i+1 < mots.length ) {_namePlayer3.setText(mots[i+1]);namePlace.put(mots[i+1],_namePlayer3);}

                    else{_namePlayer3.setText(mots[1]);namePlace.put(mots[1],_namePlayer3);}
                    playersPlace.put(_namePlayer3.getText(),_mainJoueur3);
                    imageCardPlace.put(_namePlayer3.getText(),_zoneJ3);
                    _zoneJ3.setImage(dosCarte);

                }
            }

        }else if(mots.length > 3){
            int i = 1;
            while(! mots[i].equals(client.getPseudo())) {
                i++;
            }

            if(i+1 < mots.length ) {_namePlayer2.setText(mots[i+1]);namePlace.put(mots[i+1],_namePlayer2);}
            else {_namePlayer2.setText(mots[(i+1)-(mots.length-1)]);namePlace.put(mots[(i+1)-(mots.length-1)],_namePlayer2);}
            playersPlace.put(_namePlayer2.getText(),_mainPlayer2);
            imageCardPlace.put(_namePlayer2.getText(),_zoneJ2);
            _zoneJ2.setImage(dosCarte);

            if(i+2 < mots.length ) { _namePlayer3.setText(mots[i+2]);namePlace.put(mots[i+2],_namePlayer3);}
            else {_namePlayer3.setText(mots[(i+2)-(mots.length-1)]);namePlace.put(mots[(i+2)-(mots.length-1)],_namePlayer3);}
            playersPlace.put(_namePlayer3.getText(),_mainJoueur3);
            imageCardPlace.put(_namePlayer3.getText(),_zoneJ3);
            _zoneJ3.setImage(dosCarte);

            if(mots.length == 5){
                if (i+3 < mots.length) {_namePlayer4.setText(mots[i+3]);namePlace.put(mots[i+3],_namePlayer4);}
                else {_namePlayer4.setText(mots[(i+3)-(mots.length-1)]);namePlace.put(mots[(i+3)-(mots.length-1)],_namePlayer4);}
                playersPlace.put(_namePlayer4.getText(),_mainJoueur4);
                imageCardPlace.put(_namePlayer4.getText(),_zoneJ4);
                _zoneJ4.setImage(dosCarte);
            }

        }

    }

    /**
    * @param mots, la liste des advairsaires avec leurs nombre de carte, (Joueur2:7)
    *contruit la main des joueurs adverse , le client ne verra donc que leurs nombres de cartes
    * */
    public void constructOtherHand(String[] mots) {
        String[] player;
        for (int i = 1 ; i < mots.length; i++) {
            player = mots[i].split(":");
            playersPlace.get(player[0]).setText("nombre de carte : "+player[1]);


        }

    }

    /**
    * @param str, le type de carte pour changer de couleur , ça peut etre WILD (changement de couleur)
    * ou WILD_DRAW_FOR (+4)
    *
    * cette methode ouvre un pane pour laisser au choix du joueur 4 couleurs à jouer
    */

    private void ouvrirrChoixCouleur(String str) throws IOException {


        _choiceColor.setVisible(true);
        isChoosing = true;

        _rouge.setOnMouseClicked(event -> {
            client.envoyerVerifCard(str+",RED");
            _choiceColor.setVisible(false);
            isChoosing = false;
        });

        _bleu.setOnMouseClicked(event -> {
            client.envoyerVerifCard(str+",BLUE");
            _choiceColor.setVisible(false);
            isChoosing = false;
        });

        _vert.setOnMouseClicked(event -> {
            client.envoyerVerifCard(str+",GREEN");
            _choiceColor.setVisible(false);
            isChoosing = false;
        });
        _jaune.setOnMouseClicked(event -> {
            client.envoyerVerifCard(str+",YELLOW");
            _choiceColor.setVisible(false);
            isChoosing = false;
        });

    }

    /**
    *@param color, nom de la couleur en anglai
     *
     *on reçoi le nom de la couleur choisi par un des joueur ,elle est en anglai
     *donc on la traduit en français pour l afficher sur l interface graphique
    */
    public void setColorChoisi(String color) {
        switch (color) {
            case "RED" ->{
                _colorChoisi.setText("la couleur choisi est ROUGE");
                _colorChoisi.setStyle("-fx-border-color: white; -fx-border-width: 5px; -fx-padding: 10px;");
                _colorChoisi.setFill(Color.RED);
            }
            case "BLUE" ->{
                _colorChoisi.setText("la couleur choisi est BLEU");
                _colorChoisi.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-padding: 10px;");
                _colorChoisi.setFill(Color.SKYBLUE);
            }
            case "YELLOW" ->{
                _colorChoisi.setText("la couleur choisi est JAUNE");
                _colorChoisi.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-padding: 10px;");
                _colorChoisi.setFill(Color.YELLOW);
            }
            case "GREEN" ->{
                _colorChoisi.setText("la couleur choisi est VERT");
                _colorChoisi.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-padding: 10px;");
                _colorChoisi.setFill(Color.LIGHTGREEN);
            }
        }

    }

    /**
     * @param currentPlayer , le nom du joueur courrant
     *
     * permet da fficher sur l interface le pseudo du joueur courant
     * */

    public void defCurrentPlayer(String currentPlayer){
        _currentPlayer.setText(currentPlayer);
    }

    /**
     *affiche l icone UNO a coter du pseudo su client lorsqu'il clique sur UNO
     * */
    public void myUnoValidation(){
        Image img = new Image(Objects.requireNonNull(GameController.class.getResource("/images/unoValidation.png")).toExternalForm());
        _zoneUnoValidationJ1.setImage(img);
    }


    /**
     *
     * @param mots, le nom de la carte a encaisser
     *lorsque le joueur reçoit un +2 il a le choix de d ajoutrr encore +2 pour le prochain joueur
     * */
    public void encaisser(String[] mots){


        ImageView img = new ImageView(new Image(Objects.requireNonNull(GameController.class.getResource(textToPath(mots))).toExternalForm()));
        img.setOnMouseClicked(event -> client.envoyerVerifCard(pathToText( ((ImageView)event.getTarget()).getImage()) ));
        img.setFitHeight(71);
        img.setFitWidth(51);
        _mainJoueur1.getChildren().add(img);

    }
    /**
     *
     * @param  joueur, pseudo du joueur qui s'est deconnecte
     * la methode suuivante suprimme tous les noeuds qui se trouve dans la zonne de l adversaire qui s'est deconnecter
     * */
    public void deconnexionJoueur(String joueur){
        if(playersPlace.get(joueur) != null){
            playersPlace.get(joueur).setText(null);
            playersPlace.remove(joueur);
        }

        if(namePlace.get(joueur) != null){
            namePlace.get(joueur).setText(null);
            namePlace.remove(joueur);
        }

        if(imageCardPlace.get(joueur) != null){
            imageCardPlace.get(joueur).setImage(null);
            imageCardPlace.remove(joueur);
        }
    }


    /**
     * change la scene pour la fin du jeu avec le nom du gagnant
     * */


    public void finDuJeu(){
        stage.setScene(scene4);
    }


}
