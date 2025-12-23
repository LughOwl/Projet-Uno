package application.network.thread;

import application.model.card.Card;
import application.model.card.NumberCard;
import application.model.card.special.Reverse;
import application.model.card.special.Skip;
import application.model.card.special.Wild;
import application.model.card.special.draw.DrawTwo;
import application.model.card.special.draw.WildDrawFour;
import application.model.enumUno.Color;
import application.model.enumUno.NumberEnum;
import application.model.enumUno.ScoreValue;
import application.model.exception.EndGameException;
import application.model.exception.PunishmentException;
import application.model.exception.UnoException;
import application.model.game.Game;
import application.model.players.Player;
import application.network.exception.ServerException;
import application.network.utils.DisplayCallBack;
import application.network.utils.Message;
import application.network.Server;
import application.network.UserConnection;
import jdbc.DaoJoueur;
import jdbc.DaoParticipe;
import jdbc.DaoPartie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameThread extends Thread {

    private boolean running = true;
//    private Game game;
    private Server server;
    private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private Map<UserConnection, Player> listPlayer = HashMap.newHashMap(0);

//    private Player currentPlayer = null;

    private boolean gameStarted = false;
    private boolean gameEnded = false;

    public GameThread(Server server) {
//        this.game =
        Game.getInstance();
        this.server = server;
        start();
        DisplayCallBack.log("[GameThread] GameThread lancé avec succès !");
    }

    public Game getGame() {
        return Game.getInstance();
    }

    public boolean isRunning() {
        return running;
    }

    public void removePlayer(UserConnection user) {
        if (user == null || !listPlayer.containsKey(user)) {
            DisplayCallBack.log("[GameThread] [removePlayer] Tentative de suppression d'un joueur invalide : " + user);
            return;
        }
        Player player = listPlayer.remove(user);
        try {
            getGame().removePlayer(player);
        } catch (Exception e) {
            DisplayCallBack.log("[GameThread] [removePlayer] Erreur lors de la suppression du joueur : " + e.getMessage());
            user.sendMessageError("Erreur lors de la suppression du joueur : " + e.getMessage());
            return;
        }
        DisplayCallBack.log("[GameThread] [removePlayer] Joueur supprimé de la partie : " + user.getUsername());
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void end() {
//        running = false;
        gameStarted = false;
        DisplayCallBack.log("[GameThread] [end] Changement de gameStarted. gameStarted = " + gameStarted);
        getGame().endGameScore0();
        getGame().reset();
        this.interrupt();
    }
    public void endScore0() {
//        running = false;
        gameStarted = false;
        DisplayCallBack.log("[GameThread] [end] Changement de gameStarted. gameStarted = " + gameStarted);
        getGame().endGameScore0();
        getGame().reset();
        this.interrupt();
    }

    public void setScore(String pseudo, int score) {
        if (score < 0) {
            DisplayCallBack.log("[GameThread] [setScore] Tentative de mise à jour du score avec une valeur négative.");
            return;
        }

        Player player = getGame().getPlayerByName(pseudo);
        getGame().setScore(player, score);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message message = messages.take();

                handleMessage(message);
                DisplayCallBack.log("Message traité : " + message.getContent());

            } catch (InterruptedException e) {
//                running = false;
//                Thread.currentThread().interrupt();
            }
        }
    }

    public void addMessage(String message, UserConnection userConnection) {
        if (userConnection == null) {
            throw new IllegalArgumentException("UserConnection ne peut pas être null");
        }
        DisplayCallBack.log("Message ajoute dans la liste : " + message);

        messages.add(new Message(userConnection, message));
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case "@DEMARRER_PARTIE":
                startGame(message);
                break;
            case "@CARTE_JOUEE":
                playCard(message);
                break;
            case "@UNO":
                uno(message);
                break;
            case "@END_GAME":
                endGame(message);
                break;
            case "@ENCAISSE":
                encaisse(message);
                break;
            case "@PASSER_TOUR":
                passerTour(message);
                break;
            case "@PIOCHER":
                piocher(message);
                break;

            default:
                DisplayCallBack.log("[GameThread] [handleMessage] Message non reconnu : " + message.message());
        }
    }

    private void piocher(Message message) {
        if (!gameStarted) {
            message.getUser().sendMessageError("La partie n'est pas en cours1");
            return;
        }
        Player player = getGame().getPlayerByName(message.getUser().getUsername());
        if (player == null) {
            message.getUser().sendMessageError("Joueur introuvable");
            DisplayCallBack.log("[GameThread] [piocher] Joueur introuvable pour piocher : " + message.getUser().getUsername());
            return;
        }
//        currentPlayer = getGame().getCurrentPlayer();
        if (checkPunishmentPlayer(message, player)) return;


//        Card card = game.getDrawPile().drawCard();
//        player.addCard(card);
//        try {
////            player.finishTurn();
//            Game.getInstance().moveToNextPlayer();
//            Game.getInstance().setCurrentPlayerMustPlay(true);
//        } catch (UnoException | PunishmentException e) {
//            throw new RuntimeException(e);
//        }
        try {
            player.drawCard();
        } catch (UnoException e) {
            DisplayCallBack.log("[GameThread] [piocher] Erreur lors de la pioche : " + e.getMessage());
            message.getUser().sendMessageError(e.getMessage());
            return;
        } catch (PunishmentException e) {
            try {
                punishmentPlayer(player.getName());
            } catch (UnoException | ServerException ex) {
                DisplayCallBack.log("[GameThread] [passerTour] Erreur lors de la sanction du joueur : " + e.getMessage());
            }
        }
        sendNewTas();
        sendCardAllPlayer();
        message.getUser().sendMessage("@PASSER_TOUR");

        DisplayCallBack.log("[GameThread] [piocher] Carte piochée pour " + player.getName() + " : " + player.getHand().getLast());
        server.sendMessageToAll("@CURRENT_PLAYER " + getGame().getCurrentPlayer().getName());
    }

    private void passerTour(Message message) {
        if (!gameStarted) {
            message.getUser().sendMessageError("La partie n'est pas en cours2");
            return;
        }
        Player player = getGame().getPlayerByName(message.getUser().getUsername());
        if (player == null) {
            message.getUser().sendMessageError("Joueur introuvable");
            DisplayCallBack.log("[GameThread] [passerTour] Joueur introuvable pour passer le tour : " + message.getUser().getUsername());
            return;
        }
        checkPunishmentPlayer(message, player, getGame().getCurrentPlayer());
        try {
            player.finishTurn();
//            currentPlayer = getGame().getCurrentPlayer();
        } catch (UnoException e) {
            DisplayCallBack.log("[GameThread] [passerTour] Erreur lors de la fin du tour : " + e.getMessage());
            message.getUser().sendMessageError("Vous devez jouer une carte ou piocher");
            return;
        } catch (PunishmentException e) {
            try {
                punishmentPlayer(player.getName());
            } catch (UnoException | ServerException ex) {
                DisplayCallBack.log("[GameThread] [passerTour] Erreur lors de la sanction du joueur : " + e.getMessage());
            }
        } catch (EndGameException e) {
            DisplayCallBack.log("[GameThread] [passerTour] Partie terminée : " + e.getMessage());
            endGame(message.getUser());
            return;
        }
        sendNewTas();
        sendCardAllPlayer();
        message.getUser().sendMessage("@PASSER_TOUR");
        DisplayCallBack.log("[GameThread] [passerTour] Tour passé pour " + player.getName());
        server.sendMessageToAll("@CURRENT_PLAYER " + getGame().getCurrentPlayer().getName());
    }


    private void encaisse(Message message) {
        if (!gameStarted) {
            message.getUser().sendMessageError("La partie n'est pas en cours3");
            return;
        }

        Player player = getGame().getPlayerByName(message.getUser().getUsername());
        if (player == null) {
            message.getUser().sendMessageError("Joueur introuvable");
            DisplayCallBack.log("[GameThread] [encaisse] Joueur introuvable pour encaissement : " + message.getUser().getUsername());
            return;
        }
//        currentPlayer = getGame().getCurrentPlayer();
        if (checkPunishmentPlayer(message, player)) return;

        try {
//            Card card = game.removeCardDeck();
//            sendCardAllPlayer();
//            message.getUser().sendMessage("@ENCAISSE " + card);
            player.takeDrawCard();
            sendNewTas();
            sendCardAllPlayer();
            DisplayCallBack.log("[GameThread] [encaisse] Encaissement effectué pour " + player.getName());
        } catch (UnoException e) {
            DisplayCallBack.log("[GameThread] [encaisse] Erreur lors de l'encaissement : " + e.getMessage());
            message.getUser().sendMessageError(e.getMessage());
        } catch (PunishmentException e) {
            try {
                punishmentPlayer(player.getName());
            } catch (UnoException | ServerException ex) {
                DisplayCallBack.log("[GameThread] [passerTour] Erreur lors de la sanction du joueur : " + e.getMessage());
            }
        } catch (EndGameException e) {
            DisplayCallBack.log("[GameThread] [encaisse] Partie terminée : " + e.getMessage());
//            endGame(message.getUser());
            return;
        }
        server.sendMessageToAll("@CURRENT_PLAYER " + getGame().getCurrentPlayer().getName());
    }

    private boolean checkPunishmentPlayer(Message message, Player player) {
        if (getGame().getCurrentPlayer() != player) { // CurrentPlayer
            punishmentPlayer(message, player);
            return true;
        }
        return false;
    }

    private boolean checkPunishmentPlayer(Message message, Player player, Player currentPlayer) {
        if (currentPlayer != player) { // CurrentPlayer
            punishmentPlayer(message, player);
            return true;
        }
        return false;
    }

    private void punishmentPlayer(Message message, Player player) {
        try {
            punishmentPlayer(player.getName());
            message.getUser().sendMessageError("Ce n'est pas votre tour2");
        } catch (UnoException | ServerException e) {
            DisplayCallBack.log("[GameThread] [passerTour] Erreur lors de la punition : " + e.getMessage());
            message.getUser().sendMessageError(e.getMessage());
        }
    }

    // Pk en
    private void endGame(Message message) {
        UserConnection userConnection = message.getUser();
        this.endGame(userConnection);
    }

    private void endGame(UserConnection userConnection) {
        Player player = this.listPlayer.get(userConnection);
        if (!gameStarted) {
            userConnection.sendMessageError("La partie n'est pas en cours4");
            return;
        }
        if (!player.getHand().isEmpty()) {
            return; // Ne pas terminer la partie si le joueur a encore des cartes
        }
        // Calcul de score et fin de la partie

        getGame().updateScorePlayer(player);
        getGame().endGame(player);
        resetGame();
        // Envoi le nom du gagnant à tous les joueurs
        server.sendMessageToAll("@END_GAME " + userConnection.getUsername());
        DisplayCallBack.log("[GameThread] [endGame] Partie terminée par " + userConnection.getUsername());
        userConnection.sendMessage("@FIN_PARTIE");
        gameEnded = true;
    }

    private void resetGame() {
        getGame().reset();
        listPlayer.clear();
        gameStarted = false;
        DisplayCallBack.log("[GameThread] [end] Changement de gameStarted. gameStarted = " + gameStarted);
        DisplayCallBack.log("[GameThread] [resetGame] La partie a été réinitialisée.");
    }

    private void uno(Message message) {
        Player player = getGame().getPlayerByName(message.getUser().getUsername());
        if (player == null) {
            message.getUser().sendMessageError("Joueur introuvable");
            DisplayCallBack.log("[GameThread] [uno]Joueur introuvable pour UNO : " + message.getUser().getUsername());
            return;
        }
        try {
            player.sayUno();
            message.getUser().sendMessage("@UNO " + player.getName());
            DisplayCallBack.log(player.getName() + " a dit UNO !");
        } catch (PunishmentException e) {
            try {
                punishmentPlayer(player.getName());
            } catch (UnoException | ServerException ex) {
                DisplayCallBack.log("Erreur lors de la sanction du joueur : " + e.getMessage());
            }
            message.getUser().sendMessageError("Vous devez avoir une seule carte pour dire UNO");
            DisplayCallBack.log("[GameThread] [uno]Erreur lors de la déclaration de UNO : " + e.getMessage());
        }
    }


    private void startGame(Message message) {
        if (gameStarted) {
            message.getUser().sendMessageError("La partie est déjà en cours");
            return;
        }// Réinitialiser la partie
        DisplayCallBack.log("[GameThread] [startGame] Partie en cours de lancement...");
        ArrayList<UserConnection> users = server.getListUserPseudoIsNotEmpty();
        if (users.size() < 2) {
            message.getUser().sendMessageError("Vous devez etre au moins 2 joueurs pour demarrer une partie");
            return;
        } else if (users.size() > 4) {
            message.getUser().sendMessageError("Salle des completes, maximum 4 joueurs");
            return;
        }

        if (!playerIsReady()) {
            message.getUser().sendMessageError("Tous les joueurs doivent être prêts pour démarrer la partie");
            return;
        }
        Game.getInstance().reset();


        addAllPlayersToGame();
        getGame().startGame();
        server.sendMessageToAll("@DEMARRER_PARTIE " + getGame().getStringPlayerCurrentToLast());
        server.sendMessageToAll("@TAS " + getGame().getDiscardPile().getTopCard());
        //TODO Ordre des joueurs
        //TODO Première carte
        sendCardAllPlayer();
        server.sendMessageToAll("@CURRENT_PLAYER " + getGame().getCurrentPlayer().getName());

        DisplayCallBack.log("[GameThread] [startGame] La partie commence !");
        gameStarted = true;
        DisplayCallBack.log("[GameThread] [end] Changement de gameStarted. gameStarted = " + gameStarted);
//        currentPlayer = getGame().getCurrentPlayer();
    }


    private void playCard(Message message) {
        // @CARTE_JOUEE pseudo {xxxx:xxx:xxx}  // nombre de "xxx" variable
        String[] parts = message.getContent().split(" ");
        String pseudo = parts[0];

        if (!getGame().getCurrentPlayer().getName().equals(pseudo)) {
            try {
                punishmentPlayer(pseudo);
                message.getUser().sendMessageError("Ce n'est pas votre tour3");
                return;
            } catch (ServerException e) {
                DisplayCallBack.log("[GameThread] [playCard] Erreur lors de l'envoi du message d'erreur : " + e.getMessage());
                message.getUser().sendMessageError(e.getMessage());
                return;
            } catch (UnoException e) {
                DisplayCallBack.log("[GameThread] [playCard] Erreur lors de la punition : " + e.getMessage());
                message.getUser().sendMessageError(e.getMessage());
                return;
            }
        }

        String cardString = parts[1].replace("{", "").replace("}", "");
        Card card = createCard(cardString);
        Player player = getGame().getPlayerByName(pseudo);

        if (card == null) {
            message.getUser().sendMessageError("Carte invalide : " + cardString);
            DisplayCallBack.log("[GameThread] [playCard] Carte invalide : " + cardString );
            return;
        }

        if (player == null) {
            message.getUser().sendMessageError("Joueur introuvable : " + pseudo);
            DisplayCallBack.log("[GameThread] [playCard] Joueur introuvable : " + pseudo);
            return;
        }

        try {
            // TODO : Toujours le bug avec skip et reverse même si je sais do'u il vient
//            currentPlayer = getGame().getCurrentPlayer();
            player.playCard(card);
//            currentPlayer = getGame().getCurrentPlayer();
            DisplayCallBack.log("[GameThread] [playCard] Carte jouée par " + pseudo + " : " + card + "[StringCard:"+cardString+"]");
            sendNewTas();
            sendCardAllPlayer();
            if (card instanceof Wild || card instanceof WildDrawFour) {
                server.sendMessageToAll("@CHANGER_COULEUR " + getGame().getCurrentColor());
            }
        } catch (PunishmentException e) {
            DisplayCallBack.log("[GameThread] [playCard] Punition infligée à " + player.getName() + " : " + e.getMessage());
            try {
                punishmentPlayer(pseudo);
            } catch (UnoException | ServerException ex) {
                DisplayCallBack.log("[GameThread] [playCard] Erreur lors de la punition : " + ex.getMessage());
                message.getUser().sendMessageError(ex.getMessage());
                return;
            }
            message.getUser().sendMessageError("Ce n'est pas votre tour4 :" + e.getMessage());
        } catch (UnoException e) {
            DisplayCallBack.log("[GameThread] [playCard] Erreur lors de la carte jouée : " + e.getMessage());
            message.getUser().sendMessageError("Vous n'avez pas UNO, sanction appliquée.");
        }

//        endGame(message.getUser());
    }



    private Card createCard(String cardString)  {
        String[] parts = cardString.split(",");
        String type = parts[0];
        String color = parts.length > 1 ? parts[1] : null;
        DisplayCallBack.log("[GameThread] [createCard] Création de la carte : " + cardString);
        return switch (type) {
            case "NUMBER" -> {
                String value = parts[2];
                assert value != null;
                yield new NumberCard(Color.getColor(color), NumberEnum.getValue(value), ScoreValue.fromValue(value));
            }
            case "SKIP" -> {
                assert color != null;
                yield new Skip(Color.getColor(color));
            }
            case "REVERSE" -> {
                assert color != null;
                yield new Reverse(Color.getColor(color));
            }
            case "DRAW_TWO" -> {
                assert color != null;
                yield new DrawTwo(Color.getColor(color));
            }
            case "WILD" -> {
                chooseColor(color);
                yield new Wild();
            }
            case "WILD_DRAW_FOUR" -> {
                chooseColor(color);
                yield new WildDrawFour();
            }
            default -> {
                DisplayCallBack.log("[GameThread] [createCard] Type de carte invalide : " + type);
                yield null;
            }
        };
    }

    private void chooseColor(String color) {
        assert color != null;
        Color col = Color.getColor(color);
        DisplayCallBack.log("-------------------------- Color Choisi : " + Color.getColor(color));
        getGame().setCurrentColor(col);
        if (getGame().getCurrentColor() == Color.WILD) {
            DisplayCallBack.log("[GameThread] [createCard] Couleur actuelle définie sur WILD");
        }
        DisplayCallBack.log("[GameThread] [createCard] Couleur actuelle définie sur " + getGame().getCurrentColor());

    }


    private boolean playerIsReady() {
        ArrayList<UserConnection> users = server.getListUserPseudoIsNotEmpty();
        for (UserConnection user : users) {
            if (!user.isReady()) {
                return false;
            }
        }
        return true;
    }

    private void addPlayer(UserConnection user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            DisplayCallBack.log("[GameThread] [addPlayer] Tentative d'ajout d'un joueur invalide : " + user);
            return;
        }
        Player player = new Player(user.getUsername());
        getGame().addPlayer(player);
        this.listPlayer.put(user, player);
        DisplayCallBack.log("[GameThread] [addPlayer] Joueur ajouté à la partie : " + user.getUsername());
    }

    private void addAllPlayersToGame() {
        ArrayList<UserConnection> users = server.getListUserPseudoIsNotEmpty();
        for (UserConnection user : users) {
            addPlayer(user);
        }
    }

    private void sendCard(UserConnection user) {
        Player player = listPlayer.get(user);
        if (player == null) {
            return;
        }
        StringBuilder listeCard = new StringBuilder();
        for (Card card : player.getHand()) {
            listeCard.append(card).append(" ");
        }
        user.sendMessage("@CARTE " + listeCard);
    }

    private void sendNbrCardOtherPlayer(UserConnection user) {
        // Envoi de son nombre de carte à tout le monde sauf à lui-même
        Player player = listPlayer.get(user);
        if (player == null) {
            return;
        }
        StringBuilder nbrCard = new StringBuilder();
        for (Map.Entry<UserConnection, Player> entry : listPlayer.entrySet()) {
            UserConnection otherUser = entry.getKey();
            Player otherPlayer = entry.getValue();
            if (otherUser != null && otherPlayer != null && !otherUser.equals(user)) {
                nbrCard.append(otherUser.getUsername()).append(":").append(otherPlayer.getHand().size()).append(" ");
                user.sendMessage("@NBR_CARTE_OTHER_PLAYER " + nbrCard.toString().trim());
            }
        }
        DisplayCallBack.log("[GameThread] [sendNbrCardOtherPlayer] Nombre de cartes envoyé à " + user.getUsername() + " : " + nbrCard);
    }

    private void sendCardAllPlayer() {
        DisplayCallBack.log("[GameThread] [sendCardAllPlayer] Envoi des cartes à tous les joueurs...");
        for (Map.Entry<UserConnection, Player> entry : listPlayer.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            UserConnection user = entry.getKey();
            sendCard(user);
            sendNbrCardOtherPlayer(user);
        }
        DisplayCallBack.log("[GameThread] [sendCardAllPlayer] Cartes envoyées !");
    }


    public boolean isGameStarted() {
        return gameStarted;
    }

    public void punishmentPlayer(String pseudo) throws UnoException, ServerException {
        Player player = getGame().getPlayerByName(pseudo);
        UserConnection userConnection = server.getUser(pseudo);
        if (player == null) {
            throw new UnoException("[GameThread] [punishmentPlayer] Joueur introuvable pour la punition : " + pseudo);
        }

        try {
            if (getGame().getAccumulatedDraw() != 0) {
                player.takeDrawCard();
                player.addCard(Game.getInstance().removeCardDeck());
                player.addCard(Game.getInstance().removeCardDeck());
                userConnection.sendMessageError("Vous avez été puni pour ne pas avoir encaisser le tas.");
            } else {
                player.punishment();
            }
        } catch (PunishmentException | UnoException | EndGameException e) {
            DisplayCallBack.log("[GameThread] [punishmentPlayer] Erreur lors de la punition : " + e.getMessage());
        }
        sendCardAllPlayer();
//        sendCard(userConnection);

//        currentPlayer = getGame().getCurrentPlayer();
        server.sendMessageToAll("@CURRENT_PLAYER " + getGame().getCurrentPlayer().getName());

        DisplayCallBack.log("[GameThread] [punishmentPlayer] Punition infligée à " + pseudo);
    }

    public void sendNewTas() {
        server.sendMessageToAll("@TAS " + getGame().getDiscardPile().getTopCard());
    }




//    public void setScoreForAll(int score) {
//        if (score < 0) {
//            DisplayCallBack.log("[UserConnection] [setScore] Tentative de mise à jour du score avec une valeur négative.");
//            return;
//        }
//        getGame().setScoreForAllPlayer(score);
//    }
}
