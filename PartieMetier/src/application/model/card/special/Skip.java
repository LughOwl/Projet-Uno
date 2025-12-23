package application.model.card.special;

import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

import java.util.Objects;

public class Skip extends SpecialCard {

    private final int nbrSkip = 1;

    public Skip(Color color){
        super("Skip the next player's turn", color, ScoreValue.TWENTY);
    }

    @Override
    public void playEffect() {
        for (int i = 0; i <= nbrSkip; i++) {
            Game.getInstance().setVarSkip(2);
        }
//         Game.getInstance().setCurrentPlayerIndex(Game.getInstance().getCurrentPlayerIndex() + nbrSkip);
//         Game.getInstance().setCurrentPlayer(Game.getInstance().getCurrentPlayer());
    }

    @Override
    public boolean canPlay(){
        if (Game.getInstance().getAccumulatedDraw() > 0) return false;
        if (Game.getInstance().getCurrentColor() == getColor()) return true;
        return Game.getInstance().getDiscardPile().getTopCard().getClass() == getClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Skip skip = (Skip) o;
        return getColor() == skip.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nbrSkip);
    }

    @Override
    public String toString() {
        return "Skip,"+getColor();
    }
}
