package gameplay.players;

import gameplay.card.Card;

import java.util.ArrayList;

public class Player {

    private String name;
    private int score;
    private ArrayList<Card> hand = new ArrayList<>();
    private boolean uno = false;

    public Player(String name) {
        setName(name);
        setScore(0);
    }


    //*********************//
    // Getters and setters //
    //*********************//

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        this.score = score;
    }

    public void addScore(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        this.score += value;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.add(card);
    }

    public void removeCard(Card card) {
        if (card == null || !hand.contains(card)) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.remove(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public boolean getUno() {
        return this.uno;
    }

    //****************************//
    // End of getters and setters //
    //****************************//

    /**
     * Affiche la main du joueur
     */
    public void printHand() {
        System.out.println("Hand of " + name + ":");
        for (Card card : hand) {
            System.out.println(card);
        }
    }

    public void printScore() {
        System.out.println("Score of " + name + ": " + score);
    }

    public void printPlayer() {
        System.out.println("Name: " + name);
        printScore();
        printHand();
    }

    public void play() {

    }

    public int calculScore() {
        return 0;
    }

    public void draw() {
        calculScore();
        this.clearHand();
    }

    public boolean isUno() {
        return this.hand.size() == 1;
    }

}
