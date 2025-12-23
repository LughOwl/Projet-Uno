package gameplay.card.special;

import gameplay.card.Card;
import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public abstract class SpecialCard extends Card {
    public SpecialCard(String name, String description, Color color, Type type) {
        super(name, description, color, type);
    }

    @Override
    public void play(ArrayList<Player> players, int currentPlayerIndex, int direction) {
        System.out.println("Playing card: " + getName());
        specialAction(players, currentPlayerIndex, direction);
    }

    public abstract void specialAction(ArrayList<Player> players, int currentPlayerIndex, int direction);
}
