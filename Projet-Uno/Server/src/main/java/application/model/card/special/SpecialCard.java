package application.model.card.special;

import application.model.card.Card;
import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;

public abstract class SpecialCard extends Card {

    public SpecialCard(String description, Color color, ScoreValue score) {
        super(description, color, score);
    }
}