package gameplay.card.special;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class Skip extends SpecialCard {
    public Skip(Color color){
        super("Skip", "Skip the next player's turn", color, Type.SKIP);
    }

    @Override
    public void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction) {

    }
}
