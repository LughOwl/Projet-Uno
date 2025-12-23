package gameplay.card.special;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class Wild extends SpecialCard {
    public Wild() {
        super("Wild", "Wild card", Color.WILD, Type.WILD);
    }


    @Override
    public void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction) {

    }
}
