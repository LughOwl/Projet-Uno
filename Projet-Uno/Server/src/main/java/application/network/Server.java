package application.network;

import application.network.exception.ServerException;
import application.network.thread.GameThread;
import application.network.thread.HandshakeThread;
import application.network.utils.DisplayCallBack;
import jdbc.ConnectBDD;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private ArrayList<UserConnection> usersConnections = new ArrayList<>();
    private HandshakeThread handshakeThread;

    private GameThread gameThread;
    public final int MIN_PLAYER_IN_GAME = 2;


    public Server(int port) {
        this.port = port;

//        DisplayCallBack.log("Serveur en attente de connexion sur le port " + port);
//        DisplayCallBack.log("Serveur lancer sur le port");
        DisplayCallBack.log(ConnectBDD.getInstance().toString()); // Initialize the database connection
//        gameThread = new GameThread(this);
        recreateGameThreadIfNecessary();
        handshakeThread = new HandshakeThread(this, gameThread);

//        System.out.println("Serveur lance avec succes !");
    }

    public int getPort() {
        return port;
    }

    public boolean getStatus() {
        return handshakeThread.isRunning();
    }

    public HandshakeThread getHandshakeThread() {
        return handshakeThread;
    }


    public GameThread getGameThread() {
        return gameThread;
    }

    public void recreateGameThreadIfNecessary() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(this);
            DisplayCallBack.log("[Server] [recreateGameThreadIfNecessary] Nouveau GameThread créé.");
        } else {
            DisplayCallBack.log("[Server] [recreateGameThreadIfNecessary] GameThread déjà actif.");
        }
    }



    public boolean closeConnectionForAllUser() {
        if (usersConnections == null) throw new IllegalArgumentException("[Server] [closeConnectionForAllUser] ArrayList usersConnections n'est pas initalise");
        if (usersConnections.isEmpty()) {
            DisplayCallBack.log("[Server] [closeConnectionForAllUser] Aucun utilisateur connecté.");
            return true;
        }
        List<UserConnection> toClose = new ArrayList<>(usersConnections);

        for (UserConnection user : toClose) {
            try {
                user.closeConnection();
                sendMessageToAll("@DECONNEXION_FOR " + user.getUsername());

                DisplayCallBack.log("[Server] [closeConnectionForAllUser] Connexion fermée pour l'utilisateur " + user.getUsername());
            } catch (Exception e) {
                DisplayCallBack.log("[Server] [closeConnectionForAllUser] Erreur lors de la fermeture de la connexion pour l'utilisateur " + user.getUsername());
                return false;
            }
        }
        checkStatusGame();
        gameThread.getGame().reset();
        return true;
    }


    public boolean close() {
        if (handshakeThread.isRunning()) {
            handshakeThread.end();
            DisplayCallBack.log("[Server] [close] Fermeture du serveur...");
            closeConnectionForAllUser();
            gameThread.end();
            return true;
        }
        return false;
    }

    private void stopGame() {
        if (gameThread.isGameStarted()) {
            gameThread.getGame().reset();
            DisplayCallBack.log("[Server] [stopGame] La partie a été arrêtée.");
        } else {
            DisplayCallBack.log("[Server] [stopGame] Aucune partie en cours à arrêter.");
        }
    }



    public boolean isAvailable() {
        int count = 0;
        for (UserConnection user : usersConnections) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                count++;
            }
        }
        return count < 4;
    }



    /*******************************************/
    /*              User management            */
    /*******************************************/

    public boolean addUser(UserConnection user) throws ServerException {
        if (user == null) throw new ServerException("usersConnections is null");
        if (!isAvailable()) {
            user.sendMessageError("[Server] [addUser] Le serveur est complet (4 joueurs avec pseudo connecté).");
            user.closeConnection();
            return false;
        }
        DisplayCallBack.log("[Server] [addUser] Utilisateur connecté : " + user.getUsername());

        return usersConnections.add(user);
    }

    public void removeUser(UserConnection user) throws ServerException {
        if (user == null) throw new ServerException("usersConnections is null");
        String pseudo = user.getUsername();
        if (usersConnections.remove(user)) {
//            user.closeConnection();
            DisplayCallBack.log("[Server] [removeUser] Utilisateur supprimé : " + user.getUsername());

            sendMessageToAll("@DECONNEXION_FOR " + pseudo);
            checkStatusGame();

        } else {
            DisplayCallBack.log("[Server] [removeUser] Tentative de suppression d’un utilisateur introuvable ou deja supprime : " + user.getUsername());
        }
    }

    private void checkStatusGame() {
        if (!gameThread.isGameStarted()) return;
        if (!(usersConnections.size() < MIN_PLAYER_IN_GAME)) return;
        stopGame();
    }

    public UserConnection getUser(String username) throws ServerException {
        for (UserConnection user : usersConnections) {
            if (username.equals(user.getUsername()))
                return user;
        }

        throw new ServerException("[Server] [getUser] L'utilisateur "+username+" n'existe pas...");
    }

    public ArrayList<UserConnection> getListUser() {
        return usersConnections;
    }

    public ArrayList<UserConnection> getListUserPseudoIsNotEmpty() {
        ArrayList<UserConnection> usersConnections = new ArrayList<>();
        for (UserConnection user : this.usersConnections) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                usersConnections.add(user);
            }
        }
        return usersConnections;
    }


    /**
     * Returns the list of usernames with their ready status.
     *
     * @return a string containing all usernames and their ready state ("true"/"false"),
     *         or an empty string if any user or username is invalid.
     */
    public String getListNameUser() {
        StringBuilder listUser = new StringBuilder();
        for (UserConnection user : usersConnections) {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                return "";
            }
            listUser.append(user.getUsername()).append("[").append(user.isReady()).append("] ");
        }
        return listUser.toString();
    }



    // TODO Fonctionne pas, a revoir
    public boolean checkLastPlayer() {
        if (!(this.getListUser().size() < this.MIN_PLAYER_IN_GAME)) return false;
//        gameThread.setScoreForAll(0);
//        sendMessageToAll("Nombre de joueurs insuffisant pour continuer la partie, arrêt de la partie.");
        gameThread.endScore0();
        messageAllDeconnection("Nombre de joueurs insuffisant pour continuer la partie, arrêt de la partie.");
        DisplayCallBack.log("[Server] [checkLastPlayer] Nombre de joueurs insuffisant pour continuer la partie, arrêt de la partie.");
        return true;
    }

    public void messageAllDeconnection(String message) {
        if (message == null || message.isEmpty()) {
            DisplayCallBack.log("[Server] [messageAllDeconnexion] Message vide ou null.");
            return;
        }
        List<UserConnection> copy = getListUserPseudoIsNotEmpty();
        for (UserConnection userConnection : copy) {
            if (userConnection == null) {
                DisplayCallBack.log("[Server] [messageAllDeconnexion] UserConnection null.");
                continue;
            }
            messageDeconnection(userConnection, message);
        }
    }

    private void messageDeconnection(UserConnection userConnection, String message) {
        if (userConnection == null ) {
            DisplayCallBack.log("[Server] [messageKick] UserConnection null.");
            return;
        }
        userConnection.sendMessageError(message);
        userConnection.sendMessage("@DECONNEXION " + userConnection.getUsername());
        userConnection.closeConnection();
        DisplayCallBack.log("[Server] [messageKick] Utilisateur " + userConnection.getUsername() + " a été expulsé avec le message : " + message);
    }



    public void setReadyForAllPlayer(boolean isReady) {
        for (UserConnection user : usersConnections) {
            if (user != null) {
                user.setReady(isReady);
            }
        }
    }

    /*******************************************/
    /*                Send message             */
    /*******************************************/

//    public void sendTchatPublicMessage(UserConnection utilisateur, String message) {
//        String[] newMessage = message.split(" ");
//        String m = "@PUBLIC_FROM " + utilisateur.getUsername() + " " + getContenu(Arrays.copyOfRange(newMessage, 1, newMessage.length));
//        this.userConnectionThread.sendMessage(m);
//    }

    public void sendMessageToAll(String message) {
        for (UserConnection user : usersConnections) {
            user.sendMessage(message);
        }
    }

    public void sendPublicMessageTchatUserToAll(UserConnection userConnection, String message) {
        sendMessageToAll("@PUBLIC_FROM " + userConnection.getUsername() + " " + message);
    }
    public void sendPublicMessageTchatServerToAll(String message) {
        sendMessageToAll("@SERVER_FROM " + message);
    }



}
