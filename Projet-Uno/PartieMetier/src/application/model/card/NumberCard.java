package application.model.card;

import application.model.enumUno.Color;
import application.model.enumUno.NumberEnum;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

import java.util.Objects;

public class NumberCard extends Card {

    private final NumberEnum number;

    public NumberCard(Color color, NumberEnum number, ScoreValue score) {
        super("Number card", color, score);
        this.number = number;
    }

    public NumberEnum getNumber() {
        return number;
    }


    @Override
    public void playEffect() {}

    @Override
    public boolean canPlay(){
//        if (Game.getInstance().getAccumulatedDraw() > 0) return false;
//        if (Game.getInstance().getCurrentColor() == getColor()) return true;
//        return Game.getInstance().getDiscardPile().getTopCard().getClass() == getClass() && ((NumberCard) Game.getInstance().getDiscardPile().getTopCard()).getNumber() == number;

        if (Game.getInstance().getAccumulatedDraw() > 0) return false;
        if (Game.getInstance().getCurrentColor() == getColor()) return true;

        Card topCard = Game.getInstance().getDiscardPile().getTopCard();
        if (topCard instanceof NumberCard numberCard) {
            return numberCard.getNumber() == this.number;
        }
        return false;
    }

    @Override
    public String toString() {
        return "NumberCard,"+getColor()+","+getNumber()+","+getScore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumberCard that = (NumberCard) o;
        return number == that.number && getColor() == that.getColor() && getScore() == that.getScore();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), number);
    }
}
