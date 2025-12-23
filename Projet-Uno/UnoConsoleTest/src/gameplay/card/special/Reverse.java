package gameplay.card.special;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class Reverse extends SpecialCard {
    public Reverse(Color color) {
        super("Reverse", "Reverse the order of play", color, Type.REVERSE);
    }

    @Override
    public void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction) {

    }
}
