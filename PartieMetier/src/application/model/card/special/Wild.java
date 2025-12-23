package application.model.card.special;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

public class Wild extends SpecialCard {
    public Wild() {
        super( "Wild card", Color.WILD, ScoreValue.FIFTY);
    }

    @Override
    public void playEffect(){}

    @Override
    public boolean canPlay(){
        return Game.getInstance().getAccumulatedDraw() == 0;
    }

    @Override
    public String toString() {
        return "Wild";
    }
}
