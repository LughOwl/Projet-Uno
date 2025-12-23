package application.model.card.special.draw;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

public class DrawTwo extends Draw {

    public DrawTwo(Color color) {
        super( "Draw two cards", color, ScoreValue.TWENTY, 2);
    }

    @Override
    public void playEffect() {
        Game.getInstance().setAccumulatedDraw(Game.getInstance().getAccumulatedDraw()+this.getDrawAmount());
    }

    @Override
    public boolean canPlay() {
        if (Game.getInstance().getAccumulatedDraw() > 0 && Game.getInstance().getDiscardPile().getTopCard() instanceof WildDrawFour) return false;
        if (Game.getInstance().getCurrentColor() == getColor()) return true;
        return Game.getInstance().getDiscardPile().getTopCard().getClass() == getClass();
    }

    @Override
    public String toString() {
        return "DrawTwo,"+getColor();
    }
}
