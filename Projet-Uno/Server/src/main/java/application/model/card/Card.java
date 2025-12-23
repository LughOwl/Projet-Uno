package application.model.card;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;

import java.util.Objects;

public abstract class Card {
    private String description;
    private Color color;
    private ScoreValue score;

    public Card(String description, Color color, ScoreValue score) {
        this.description = description;
        this.color = color;
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getScore() {
        return score.getValue();
    }

    public void setScore(ScoreValue score) {
        this.score = score;
    }

    /**
     * This method is called when a player plays a card.
     * It will execute the effect of the card on the game.
     */
    public abstract void playEffect();

    /**
     * This method is called when a player plays a card.
     * It will say if a player can play a card.
     */
    public abstract boolean canPlay();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return color == card.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return "Card{" +
                "description='" + description + '\'' +
                ", color=" + color +
                ", score=" + score +
                '}';
    }
}
