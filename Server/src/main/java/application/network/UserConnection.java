package application.network;

import application.network.exception.ServerException;
import application.network.thread.GameThread;
import application.network.thread.UserConnectionThread;
import application.network.utils.DisplayCallBack;
import jdbc.DaoJoueur;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserConnection {
    private final UserConnectionThread userConnectionThread;
    private GameThread gameThread;

    private String username;
    private Server server;
    private Socket socket;
    private boolean connected;
    private boolean isReady = false;

        // Constante
//    private final int MIN_PLAYER_IN_GAME = 2;


    public UserConnection(Socket socket, Server server, GameThread gameThread) {
        this.socket = socket;
        this.server = server;

        this.userConnectionThread = new UserConnectionThread(this, socket);
        this.gameThread = gameThread;
//        username = "user" + (int) (Math.random() * 1000);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Server getServer() {
        return server;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public UserConnectionThread getUserConnectionThread() {
        return userConnectionThread;
    }

    public GameThread getGameThread() {
        return gameThread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConnection that = (UserConnection) o;
        return connected == that.connected && Objects.equals(userConnectionThread, that.userConnectionThread) && Objects.equals(username, that.username) && Objects.equals(server, that.server) && Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userConnectionThread, username, server, socket, connected);
    }

    @Override
    public String toString() {
        return "UserConnection{" +
                "userConnectionThread=" + userConnectionThread +
                ", gameThread=" + gameThread +
                ", username='" + username + '\'' +
                ", server=" + server +
                ", socket=" + socket +
                ", connected=" + connected +
                ", isReady=" + isReady +
                '}';
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    /*
    Message envoyé lorsque le joueur connecté donne son pseudo. Par exemple @CONNEXION Alice"
    */
    private final static String regexCONNEXION = "^@CONNEXION \\p{Alnum}+$";
    private final static String regexDECONNEXION = "^@DECONNEXION$";			// évident
    private final static String regexTO_ALL = "^@TO_ALL .*$";
    private final static String regexPUBLIC_FROM = "^@PUBLIC_FROM \\p{Alnum}+ .*$";
    private final static String regexDEMARRER = "^@DEMARRER_PARTIE$";			// Lorsque le joueur 1 décide de démarrer la partie

    /*
    Message envoyé par le joueur quand il pose une carte. Par exemple @CARTE_JOUEE 2 Jaune
    */
    private final static String regexCARTE_JOUEE = "^@CARTE_JOUEE \\w+ \\{.*\\}$";//"^@CARTE_JOUEE \\w+ (?:\\w+,)*\\w+$";

    private final static String regexFIN_TOUR = "^@FIN_TOUR$";					// évident
    private final static String regexPIOCHE = "^@PIOCHER$";						// évident

    /*
    Lorsque le joueur courant accepte d'encaisser un +2 (ou une pile de +2 si vous jouez comme ça)
    */
    private final static String regexENCAISSE = "^@ENCAISSE";
    private final static String regexUNO = "^@UNO$";
    private final static String regexPASSER_TOUR = "^@PASSER_TOUR$";

    private final static String regexISREADYCLIENT = "^@PLAYER_IS_READY";
    private final static String regexISNOTREADYCLIENT = "^@PLAYER_IS_NOT_READY";

    private final static String regexENDGAME = "^@ENDGAME$";
    private final static String regexREJOUER = "^@REJOUER";

    private final static String[] protocol ={
            regexCONNEXION,
            regexDECONNEXION,
            regexTO_ALL,
            regexPUBLIC_FROM,
            regexDEMARRER,

            regexCARTE_JOUEE,
            regexFIN_TOUR,
            regexPIOCHE,

            regexENCAISSE,
            regexUNO,
            regexPASSER_TOUR,

            regexISREADYCLIENT,
            regexISNOTREADYCLIENT,

            regexENDGAME,
            regexREJOUER
    };


    public void handlePacket(String message) {
        if (message == null) {
            connected = false;
            try {
                server.removeUser(this);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (!checkProtocol(message)) {
            this.userConnectionThread.sendMessageError("Vous n'utilisez pas un protocol autoriser par le serveur !");
            DisplayCallBack.log("[UserConnection] [handlePacket] Le message de "+this+" ne respecte pas le protocole : " + message);
            return;
        }

        String messageType = message.split(" ")[0];
        switch (messageType) {
            case "@CONNEXION" -> handleConnection(message);
            case "@REJOUER" -> handleRePlayer(message);
            case "@DECONNEXION" -> handleDisconnection();
            case "@TO_ALL" -> traiterTO_ALL(message);
            case "@PLAYER_IS_READY" -> handleIsReady();
            case "@PLAYER_IS_NOT_READY" -> handleIsNotReady();
            default -> sendToGame(message);
        }
    }

    private void handleRePlayer(String message) {
        if (!gameThread.isGameEnded()) {
            this.userConnectionThread.sendMessageError("La partie n'est pas terminée, vous ne pouvez pas la relancer.");
            return;
        }
        server.setReadyForAllPlayer(false);
        updateListUserToAll();
        this.sendMessage("@REJOUER");

    }


    private void handleConnection(String message) {
        String[] mots = message.split(" ");
        if (gameIsFull()) {
            this.userConnectionThread.sendMessageError("Le serveur est plein, vous ne pouvez pas vous connecter");
            this.closeConnection();
            return;
        }
        if (gameThread.isGameStarted()) {
            this.userConnectionThread.sendMessageError("La partie a déjà commencé, vous ne pouvez pas vous connecter");
            this.closeConnection();
            return;
        }
        try {
            server.getUser(mots[1]);
            this.userConnectionThread.sendMessageError("Vous n'etes pas connecte, le pseudo existe deja");
        } catch (ServerException e) {
            this.username = mots[1];
            this.userConnectionThread.sendMessage("@CONNEXION_OK");
            updateListUserToAll();
            server.sendPublicMessageTchatServerToAll("Le joueur "+this.username+" viens de se connecter");
            addPlayerToBDD(this);
        }
    }

    private boolean gameIsFull() {
        if (server.getListUserPseudoIsNotEmpty().size() == 4) {
            clearUserPseudoIsEmpty();
            return true;
        }
        return false;
    }

    private void clearUserPseudoIsEmpty() {
        for (UserConnection user : server.getListUser()) {
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                try {
                    server.removeUser(user);
                } catch (ServerException e) {
                    DisplayCallBack.log("[UserConnection] [clearUserPseudoIsEmpty] Erreur lors de la suppression de l'utilisateur " + user.getUsername());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleDisconnection() {
        sendPublicMessageTchatServerToAll("Le joueur " + this.username + " viens de se déconnecter");
        sendMessage("@DECONNEXION " + this.username);
        closeConnection();
        if (!server.checkLastPlayer())
            sendMessageToAll("@DECONNEXION_FOR " + this.username);
        gameThread.removePlayer(this);
    }


    public void updateListUserToAll() {
        for (UserConnection user : server.getListUser()) {
            user.userConnectionThread.sendMessage("@LISTUSERS " + server.getListNameUser());
        }
    }

    private void traiterTO_ALL(String message) {
        sendPublicMessageTchatUserToAll(message);
    }

    private void handleIsReady() {
        this.isReady = true;
        sendMessageToAll("@PLAYER_IS_READY " + this.username);
        DisplayCallBack.log("[UserConnection] [handleIsReady] "+this.username + " est pret");
    }

    private void handleIsNotReady() {
        this.isReady = false;
        sendMessageToAll("@PLAYER_IS_NOT_READY " + this.username);
        DisplayCallBack.log("[UserConnection] [handleIsReady] "+this.username + " n'est pas pret");
    }

    public boolean checkProtocol(String message) {
        for (String protocol : protocol) {
            if (checkMessage(message, protocol))
                return true;
        }
        return false;
    }

    public boolean checkMessage(String message, String protocol) {
        Pattern pattern = Pattern.compile(protocol, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).matches();
    }





    private String getContenu(String[] mots) {
        String contenu = "";
        for (int i=0; i<mots.length-1; i++) {
            contenu += mots[i]+" ";
        }
        contenu += mots[mots.length-1];
        return contenu;
    }


    private void addPlayerToBDD(UserConnection user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            DisplayCallBack.log("[Server] [addPlayerToBDD] Tentative d'ajout d'un utilisateur invalide, pseudo ou joueur inexistant.");
            return;
        }

        if (DaoJoueur.getOrAddPlayerId(user.getUsername()) != -1) {
            DisplayCallBack.log("[Server] [addPlayerToBDD] L'utilisateur " + user.getUsername() + " existe déjà dans la base de données.");
        } else
            DisplayCallBack.log("[Server] [addPlayerToBDD] Ajout de l'utilisateur " + user.getUsername() + " à la base de donnée.");
    }


    public void closeConnection() {
        try {
            DisplayCallBack.log("[UserConnection] [closeConnection] Deconnexion du serveur de l'utilisateur " + this.username + " en cours...");
            sendMessage("@DECONNEXION " + this.username);
            this.server.removeUser(this);
            this.socket.close();
            this.userConnectionThread.end();
        } catch (IOException | ServerException e) {
            throw new RuntimeException(e);
        }
    }


    /*******************************************/
    /*                Send message             */
    /*******************************************/

    public void sendPublicMessageTchatServerToAll(String message) {
        server.sendPublicMessageTchatServerToAll(message);
    }

    public void sendPublicMessageTchatUserToAll(String message) {
        server.sendPublicMessageTchatUserToAll(this, message);
    }

    public void sendMessage(String message) {
        this.userConnectionThread.sendMessage(message);
    }

    public void sendMessageToAll(String message) {
        server.sendMessageToAll(message);
    }
    public void sendMessageError(String message) {
        this.userConnectionThread.sendMessageError(message);
    }

    private void sendToGame(String message) {
        if (gameThread == null) {
            DisplayCallBack.log("[UserConnection] [sendToGame] Le GameThread est null, le message ne peut pas être envoyé.");
        }  else if (!gameThread.isRunning()) {
            DisplayCallBack.log("[UserConnection] [sendToGame] Le GameThread n'est pas en cours d'exécution, le message ne peut pas être envoyé.");
        } else
            gameThread.addMessage(message, this);
    }








}
