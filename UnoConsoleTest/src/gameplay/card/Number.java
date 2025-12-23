package gameplay.card;

import gameplay.enumUno.Color;
import gameplay.enumUno.NumberEnum;
import gameplay.enumUno.Type;
import gameplay.players.Player;

import java.util.ArrayList;

public class Number extends Card {

    private final NumberEnum value;

    public Number(Color color, NumberEnum value) {//String name, String description, Color color, NumberEnum value) {
//        super(name, description, color, Type.NUMBER);
        super("Number", "Number card", color, Type.NUMBER);
        this.value = value;
    }

    public NumberEnum getValue() {
        return value;
    }

    @Override
    public void play(ArrayList<Player> players, int currentPlayerIndex, int direction) {}

    @Override
    public String toString() {
        return "" + value;
    }
}
