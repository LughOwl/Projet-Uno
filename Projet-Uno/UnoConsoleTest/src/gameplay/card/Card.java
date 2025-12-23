package gameplay.card;

import gameplay.enumUno.Color;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public abstract class Card {
    private final String name;
    private final String description;
    private final Color color;
    private final Type type;

    public Card(String name, String description, Color color, Type type) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.type = type;
    }

    public abstract void play(ArrayList<Player> players, int currentPlayerIndex, int direction);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }
}
