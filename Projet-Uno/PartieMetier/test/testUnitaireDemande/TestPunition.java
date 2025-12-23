package testUnitaireDemande;

import application.model.card.NumberCard;
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

import static org.junit.jupiter.api.Assertions.*;

public class TestPunition {
    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    private static final NumberCard green2 = new NumberCard( Color.GREEN, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard yellow6 = new NumberCard(Color.YELLOW, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard red1 = new NumberCard(Color.RED, NumberEnum.ONE, ScoreValue.ONE);
    private static final NumberCard blue2 = new NumberCard(Color.BLUE, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard yellow4 = new NumberCard(Color.YELLOW, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard red9 = new NumberCard(Color.RED, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard blue9 = new NumberCard(Color.BLUE, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard blue7 = new NumberCard(Color.BLUE, NumberEnum.SEVEN, ScoreValue.SEVEN);
    private static final NumberCard blue0 = new NumberCard(Color.BLUE, NumberEnum.ZERO, ScoreValue.ZERO);
    private static final NumberCard green8 = new NumberCard(Color.GREEN, NumberEnum.EIGHT, ScoreValue.EIGHT);
    private static final NumberCard red4 = new NumberCard(Color.RED, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard blue5 = new NumberCard(Color.BLUE, NumberEnum.FIVE, ScoreValue.FIVE);
    private static final NumberCard green0 = new NumberCard(Color.GREEN, NumberEnum.ZERO, ScoreValue.ZERO);

    @BeforeEach
    public void initPlayer() {
        alice = new Player("Alice");
        bob = new Player("Bob");
        charles = new Player("Charles");

        alice.addCard(green2);
        alice.addCard(yellow6);
        alice.addCard(red1);

        bob.addCard(blue2);
        bob.addCard(yellow4);
        bob.addCard(red9);

        charles.addCard(blue9);
        charles.addCard(blue7);
        charles.addCard(blue0);
    }

    @BeforeEach
    public void initGame() {
        game.reset();
        game.addPlayer(alice, bob, charles);

        Discard discardPile = game.getDiscardPile();
        discardPile.addCard(green8);

        Deck deck = game.getDrawPile();
        deck.addCard(yellow6);
        deck.addCard(red4);
        deck.addCard(green2);
        deck.addCard(blue5);
        deck.addCard(green0);
    }

    @Test
    public void TestDeLaPunitionPourUnCoupIllegalDAlice() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        assertThrows(PunishmentException.class, () -> alice.playCard(yellow6));
        alice.punishment();
        assertEquals(bob, game.getCurrentPlayer());
        assertEquals(5,alice.getHand().size());
        assertTrue(alice.getHand().contains(yellow6));
        assertTrue(alice.getHand().contains(red4));
        assertEquals(green2, game.getDrawPile().getTopCard());
    }

    @Test
    public void TestDUneActionDeBobLorsqueCeNEstPasSonTour() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        assertThrows(PunishmentException.class, () -> bob.drawCard());
        bob.punishment();
        assertEquals(alice, game.getCurrentPlayer());
        assertEquals(5,bob.getHand().size());
        assertTrue(bob.getHand().contains(yellow6));
        assertTrue(bob.getHand().contains(red4));
        assertEquals(green2, game.getDrawPile().getTopCard());
    }
}
