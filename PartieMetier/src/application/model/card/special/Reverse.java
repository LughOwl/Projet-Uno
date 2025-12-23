package application.model.card.special;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

public class Reverse extends SpecialCard {
    public Reverse(Color color) {
        super( "Reverse the order of play", color, ScoreValue.TWENTY);
    }

    @Override
    public void playEffect() {
        Game.getInstance().changeDirection();
    }

    @Override
    public boolean canPlay(){
        if (Game.getInstance().getAccumulatedDraw() > 0) return false;
        if (Game.getInstance().getCurrentColor() == getColor()) return true;
        return Game.getInstance().getDiscardPile().getTopCard().getClass() == getClass();
    }

    @Override
    public String toString() {
        return "Reverse,"+getColor();
    }
}
