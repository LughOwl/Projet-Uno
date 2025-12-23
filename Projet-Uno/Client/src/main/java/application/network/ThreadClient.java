package application.network;

import application.controller.*;
import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * La classe ClientChat crée un thread pour traiter les messages reçus du serveur (voir ThreadConsole).
 * La classe est centrée
 */
public class ThreadClient {
    private static final String SERVEUR = "localhost";
    private static final int PORT = 4567;

    private ControllerScene1 cs1;
    private ControllerScene2 cs2;
    private GameController cs3;
    private TchatController csTchat;
    private FinalGameController cs4; // utile pour accéder à la méthode afficherConsole
    private ThreadConsole threadConsole; // gère la réception des messages du serveur
    private Socket socket; // La connexion
    private PrintWriter out; // Le flux vers le serveur (le flux d'entrée est uniquement utile dans le thread)
    private String pseudo; // Le pseudo de ce client
    private int score; // Le score de ce client
    private boolean estPret = false;

    public ThreadClient(ControllerScene1 monCs1, ControllerScene2 monCs2, GameController monCs3, FinalGameController monCs4,TchatController monTchat) {
        this.cs1 = monCs1;
        this.cs2 = monCs2;
        this.cs3 = monCs3;
        this.cs4 = monCs4;
        this.csTchat = monTchat;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPseudo() { return pseudo; }

    public void setEstPret(boolean estPret) {
        this.estPret = estPret;
    }

    /**
     * La méthode afficherMessage est appelée par le threadConsole lorsqu'un message a été reçu du serveur.
     * Il faut savoir quel est le type du message pour éventuellement l'afficher d'une certaine manière dans
     * l'interface (dans cette application, tout s'affiche dans la console mais on pourrait imaginer une
     * fenêtre pour les messages publics, une autre pour les messages privés et encore une autre pour les
     * erreurs, etc.)
     * @param message Le message reçu du serveur
     */
    public void afficherMessage(String message) {
        Platform.runLater(() -> {
            String[] mots = message.split(" ");
            switch (mots[0]) {
                case "@PUBLIC_FROM" -> afficherMessagePublic(mots);
                case "@SERVER_FROM" -> afficherMessageServeur(mots);
                case "@ERROR" -> afficherErreur(mots);
                case "@CONNEXION_OK" -> afficherSalleAttente();
                case "@DECONNEXION" -> afficherDeconnexion(mots[1]);
                case "@LISTUSERS" -> afficherListeJoueur(mots);
                case "@PLAYER_IS_READY" -> afficherJoueurPret(mots[1]);
                case "@PLAYER_IS_NOT_READY" -> afficherJoueurPasPret(mots[1]);
                case "@DEMARRER_PARTIE" -> afficherPartie(mots);
                case "@PASSER_TOUR" -> enleverUnoLogo();
                case "@CURRENT_PLAYER"->afficherTour(mots[1]);
                case "@CARTE"-> afficherMesCartes(mots);
                case "@CHANGER_COULEUR" -> affichecolor(mots[1]);
                case "@ENCAISSE"-> afficheCarteEncaisser(mots[1]);
                case "@NBR_CARTE_OTHER_PLAYER"->afficherCarteAdversaire(mots);
                case "@UNO"->afficherUnoValidation();
                case "@REJOUER" -> traiterRejouer();
                case "@TAS"->afficherCarteJouee(mots);
                case "@DECONNEXION_FOR" -> enleverJoueur(mots[1]);
                case "@END_GAME" -> finDuJeu(mots[1]);
            }
        });
    }

    private void traiterRejouer() {
        cs4.retourFile();
        cs2.afficherJoueurPasPret(pseudo);
    }

    private void enleverJoueur(String mot) {
        cs3.deconnexionJoueur(mot);
    }

    private void enleverUnoLogo() {
        cs3.enleverUno();
    }

    private void affichecolor(String mot) {
            cs3.setColorChoisi(mot);
    }

    private void afficherTour(String mot) {
        cs3.defCurrentPlayer("tour : "+mot);
    }

    public void envoyerEncaisser(){
        out.println("@ENCAISSE");
    }

    public void envoyerPasseTour(){out.println("@PASSER_TOUR");}

    private void afficheCarteEncaisser(String card) {
        String[] cardSplit = card.split(",");
        cs3.encaisser(cardSplit);

    }

    private void afficherCarteAdversaire(String[] mots) {
        cs3.constructOtherHand(mots);
    }

    public void envoyerVerifCard(String card){


        out.println("@CARTE_JOUEE "+getPseudo()+" {"+card+"}");
        //System.out.println("@CARTE_JOUEE "+getPseudo()+" {"+card+"}");


    }
    private void afficherCarteJouee(String[] mots) {
        String[] card = mots[1].split(",");

        cs3.ajoutSabot(card);

    }


    // permet de construire la main du joueur dans l interface graphique
    private void afficherMesCartes(String[] mots) {
        cs3.constructHand(mots);
    }

    private void afficherMessagePublic(String[] mots) {
        String str = mots[1]+" : "+getContenu(Arrays.copyOfRange(mots, 3, mots.length));
        cs2.afficherMessage(str);
        csTchat.afficherMessage(str);
    }

    private void afficherMessageServeur(String[] mots) {
        String str = getContenu(Arrays.copyOfRange(mots, 1, mots.length));
        cs2.afficherMessage(str);
    }

    void afficherErreur(String[] mots) {
        String str;
        if (mots[0].contains("@ERROR"))
            str = getContenu(Arrays.copyOfRange(mots, 1, mots.length));
        else
            str = Arrays.toString(mots);
        cs1.afficherErreur(str);
    }

    private void afficherSalleAttente() {
        cs1.afficherSalleAttente();
    }

    private void afficherDeconnexion(String joueur) {
        cs2.afficherDeconnexion(joueur);
    }

    public void afficherDeconnexionServeur(){
        afficherDeconnexion(pseudo);
        afficherErreur(new String[]{"Connexion interrompue avec le serveur"});
    }

    private void afficherListeJoueur(String[] mots) {
        String str = getContenu(Arrays.copyOfRange(mots, 1, mots.length));
        cs2.afficherListeJoueur(str);
    }

    private void afficherJoueurPret(String joueur) {
        cs2.afficherJoueurPret(joueur);
    }

    private void afficherJoueurPasPret(String joueur) {
        cs2.afficherJoueurPasPret(joueur);
    }

    private void afficherPartie(String[] joueur) {
        cs2.afficherPartie();
        cs3.defCurrentPlayer(joueur[1]);
        cs3.placePlayersName(joueur);
    }

    private String getContenu(String[] mots) {
        if (mots.length == 0) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (int i=0; i<mots.length-1; i++) {
            str.append(mots[i]).append(" ");
        }

        str.append(mots[mots.length - 1]);

        return str.toString();
    }


    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer la connexion. Cette méthode est appelée
     * par le bouton "Connexion!"
     */
    public void envoyerConnexion() {
        try {
            socket = new Socket(SERVEUR, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            threadConsole = new ThreadConsole(this);
            out.println("@CONNEXION " + this.pseudo);
        }
        catch (IOException e) {
            cs1.afficherErreur("Le serveur n'est pas lancé");
        }

    }

    public void envoyerUno(){

        out.println("@UNO");

    }

    private void afficherUnoValidation(){
        cs3.myUnoValidation();
    }





    public void envoyerPret() {
        if (estPret) {
            out.println("@PLAYER_IS_NOT_READY");
            estPret = false;
        }
        else {
            out.println("@PLAYER_IS_READY");
            estPret = true;
        }
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer la déconnexion. Cette méthode est appelée
     * par le bouton de fermeture de la fenêtre
     */
    public void envoyerDeconnexion() {
        cs2.afficherDeconnexion(pseudo);
        this.out.println("@DECONNEXION");
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer l'envoi d'un message public.
     * Cette méthode est appelée par le bouton "Public"
     * @param message C'est le contenu du la zone de message
     */
    public void envoyerMessagePublic(String message) {
        this.out.println("@TO_ALL " + message);
    }

    public void envoyerLancerPartie() {
        this.out.println("@DEMARRER_PARTIE");
    }




    public void finDuJeu(String winner) {
        cs4.setWinnerName(winner);
        cs3.finDuJeu();

    }

    public void envoyerPioche() {
        out.println("@PIOCHER");
    }

    public void envoyerRejouer() {
        out.println("@REJOUER");
    }
}
