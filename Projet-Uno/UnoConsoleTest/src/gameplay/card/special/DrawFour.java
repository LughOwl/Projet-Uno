package gameplay.card.special;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class DrawFour extends SpecialCard {

    private final int drawAmount = 4;

    public DrawFour() {
        super("DrawFour", "Draw two cards and change the color", Color.WILD, Type.WILD_DRAW_FOUR);
    }

    public int getDrawAmount() {
        return drawAmount;
    }

    @Override
    public void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction) {

    }
}
