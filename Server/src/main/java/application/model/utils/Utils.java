package application.model.utils;

import application.model.card.NumberCard;
import application.model.card.special.Reverse;
import application.model.card.special.Skip;
import application.model.card.special.Wild;
import application.model.card.special.draw.DrawTwo;
import application.model.card.special.draw.WildDrawFour;
import application.model.enumUno.Color;
import application.model.enumUno.NumberEnum;
import application.model.enumUno.ScoreValue;
import application.model.game.Game;

public class Utils {
    /**
     * Creates and initializes all the cards required for the game and adds them to the draw pile.
     * <br><br>
     * This method generates a complete set of cards, including:
     * - One NumberCard with a value of ZERO for each color (excluding WILD).
     * - Two sets of NumberCards for each color (excluding ZERO) and special cards like
     *   DrawTwo, Reverse, and Skip for each color (excluding WILD).
     * - Four Wild cards and four WildDrawFour cards.
     * <br><br>
     * The `addCardsForColor` helper method is used to add all non-ZERO NumberCards
     * and special cards for a specific color.
     *
     * @param game The game instance to which the cards are added
     */
    public static void createCards(Game game) {
        for (Color color : Color.values()) {
            if (color != Color.WILD) {
                game.getDrawPile().addCard(new NumberCard(color, NumberEnum.ZERO, ScoreValue.ZERO));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (Color color : Color.values()) {
                if (color != Color.WILD) {
                    addCardsForColor(game, color);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            game.getDrawPile().addCard(new Wild());
            game.getDrawPile().addCard(new WildDrawFour());
        }
    }

    /**
     * Adds all non-ZERO NumberCards and special cards (DrawTwo, Reverse, Skip)
     * for the specified color to the draw pile.
     * <br><br>
     * This method is a helper for `createCards` to ensure the cards for each
     * color are added systematically.
     *
     * @param game  The game instance to which the cards are added
     * @param color The color for which cards are being added
     */
    private static void addCardsForColor(Game game, Color color) {
        for (NumberEnum number : NumberEnum.values()) {
            if (number != NumberEnum.ZERO) {
                game.getDrawPile().addCard(new NumberCard(color, number, ScoreValue.valueOf(number.name())));
            }
        }
        game.getDrawPile().addCard(new DrawTwo(color));
        game.getDrawPile().addCard(new Reverse(color));
        game.getDrawPile().addCard(new Skip(color));
    }
}

