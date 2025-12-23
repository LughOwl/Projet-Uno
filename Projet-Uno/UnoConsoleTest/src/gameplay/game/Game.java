package gameplay.game;

import gameplay.card.Card;
import gameplay.players.Player;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private int direction = 1;

    private Discard discardPile;
    private Deck drawPile;
    private ArrayList<Round> rounds = new ArrayList<>();

    public Game(ArrayList<Player> players, int numberOfDecks) {
        setPlayers(players);
        setDiscardPile(new Discard());
        setDrawPile(new Deck(numberOfDecks));
    }

    //*********************//
    // Getters and setters //
    //*********************//

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null or empty");
        }
        this.players = players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if (currentPlayerIndex < 0) {
            throw new IllegalArgumentException("Current player index cannot be negative");
        }
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void changeDirection() {
        this.direction *= -1;
    }

    public Discard getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(Discard discardPile) {
        if (discardPile == null) {
            throw new IllegalArgumentException("Discard pile cannot be null");
        }
        this.discardPile = discardPile;
    }

    public Deck getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(Deck drawPile) {
        if (drawPile == null) {
            throw new IllegalArgumentException("Draw pile cannot be null");
        }
        this.drawPile = drawPile;
    }

    public ArrayList<Round> getRounds() {
        return rounds;
    }

    public void addRound(Round round) {
        if (round == null) {
            throw new IllegalArgumentException("Round cannot be null");
        }
        rounds.add(round);
    }

    //****************************//
    // End of getters and setters //
    //****************************//


    public void startGame() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCard(drawPile.drawCard());
            }
        }
        discardPile.addCard(drawPile.drawCard());
    }

    public void endGame() {
        for (Player player : players) {
            player.getHand().clear();
        }
        discardPile.clear();
        drawPile.clear();
        rounds.clear();
    }

    public void nextPlayer() {
        currentPlayerIndex = currentPlayerIndex + direction;
        if (currentPlayerIndex < 0) {
            currentPlayerIndex = players.size() - 1;
        } else if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
    }

    public void playCard(int cardIndex) {
        Card card = players.get(currentPlayerIndex).getHand().get(cardIndex);

    }


}
