package gameplay.card.special;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class DrawTwo extends SpecialCard {

    private final int drawAmount = 2;

    public DrawTwo(Color color) {
        super("Draw Two", "Draw two cards", color, Type.DRAW_TWO);
    }

    public int getDrawAmount() {
        return drawAmount;
    }

    @Override
    public void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction) {

    }
}
