package application.model.card.special.draw;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

public class WildDrawFour extends Draw {

    public WildDrawFour() {
        super("Draw four cards and change the color", Color.WILD, ScoreValue.FIFTY,4);
    }

    @Override
    public void playEffect()  {
        Game.getInstance().setAccumulatedDraw(Game.getInstance().getAccumulatedDraw()+this.getDrawAmount());
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public String toString() {
        return "WildDrawFour";
    }
}
