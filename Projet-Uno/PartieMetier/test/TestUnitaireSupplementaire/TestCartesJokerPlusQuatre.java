package TestUnitaireSupplementaire;

import application.model.card.NumberCard;
import application.model.card.special.Wild;
import application.model.card.special.draw.DrawTwo;
import application.model.card.special.draw.WildDrawFour;
import application.model.enumUno.Color;
import application.model.enumUno.NumberEnum;
import application.model.enumUno.ScoreValue;
import application.model.exception.PunishmentException;
import application.model.exception.UnoException;
import application.model.game.Deck;
import application.model.game.Discard;
import application.model.game.Game;
import application.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCartesJokerPlusQuatre {
    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    private static final DrawTwo blueDrawTwo = new DrawTwo(Color.BLUE);
    private static final NumberCard blue9 = new NumberCard(Color.BLUE, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard yellow4 = new NumberCard(Color.YELLOW, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard yellow6 = new NumberCard(Color.YELLOW, NumberEnum.SIX, ScoreValue.SIX);
    private static final WildDrawFour wildDrawFour = new WildDrawFour();
    private static final NumberCard blue7 = new NumberCard(Color.BLUE, NumberEnum.SEVEN, ScoreValue.SEVEN);
    private static final NumberCard blue1 = new NumberCard(Color.BLUE, NumberEnum.ONE, ScoreValue.ONE);
    private static final DrawTwo greenDrawTwo = new DrawTwo(Color.GREEN);
    private static final NumberCard green1 = new NumberCard(Color.GREEN, NumberEnum.ONE, ScoreValue.ONE);
    private static final NumberCard green9 = new NumberCard(Color.GREEN, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard blue0 = new NumberCard(Color.BLUE, NumberEnum.ZERO, ScoreValue.ZERO);
    private static final NumberCard green8 = new NumberCard(Color.GREEN, NumberEnum.EIGHT, ScoreValue.EIGHT);
    private static final NumberCard green2 = new NumberCard( Color.GREEN, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard red4 = new NumberCard(Color.RED, NumberEnum.FOUR, ScoreValue.FOUR);

    @BeforeEach
    public void initPlayer() {
        alice = new Player("Alice");
        bob = new Player("Bob");
        charles = new Player("Charles");

        alice.addCard(blueDrawTwo);
        alice.addCard(blue9);
        alice.addCard(yellow4);

        bob.addCard(yellow6);
        bob.addCard(wildDrawFour);
        bob.addCard(blue7);

        charles.addCard(blue1);
        charles.addCard(greenDrawTwo);
        charles.addCard(green1);
    }

    @BeforeEach
    public void initGame() {
        game.reset();
        game.addPlayer(alice, bob, charles);

        wildDrawFour.setColor(Color.WILD);

        Discard discardPile = game.getDiscardPile();
        discardPile.addCard(green9);

        Deck deck = game.getDrawPile();
        deck.addCard(blue0);
        deck.addCard(green8);
        deck.addCard(green2);
        deck.addCard(red4);
        deck.addCard(green2);
    }

    @Test
    public void TestDUnEmpilementDUnPlusDeuxSuivitDUnPlusQuatre() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(blue9);
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        assertEquals(3,bob.getHand().size());
        bob.playCard(blue7);
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        charles.playCard(blue1);
        charles.finishTurn();

        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(blueDrawTwo);
        alice.sayUno();
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        bob.playCard(wildDrawFour);
        bob.chooseWildColor(Color.GREEN);
        bob.sayUno();
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        charles.takeDrawCard();
        assertEquals(8, charles.getHand().size());
        assertEquals(alice, game.getCurrentPlayer());
    }

    @Test
    public void TestDUnEmpilementIllegalDUnPlusQuatreSuivitDUneCarteNombre() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(blue9);
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        assertEquals(3,bob.getHand().size());
        bob.playCard(blue7);
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        charles.playCard(blue1);
        charles.finishTurn();

        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(blueDrawTwo);
        alice.sayUno();
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        bob.playCard(wildDrawFour);
        bob.chooseWildColor(Color.GREEN);
        bob.sayUno();
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        assertThrows(PunishmentException.class, () -> charles.playCard(green1));
        charles.takeDrawCard();
        assertEquals(8, charles.getHand().size());
        assertEquals(alice, game.getCurrentPlayer());
    }
}
