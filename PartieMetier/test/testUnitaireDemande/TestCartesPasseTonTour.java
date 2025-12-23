package testUnitaireDemande;


import application.model.card.NumberCard;
import application.model.card.special.Skip;
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

public class TestCartesPasseTonTour {
    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    private static final Skip redSkip = new Skip(Color.RED);
    private static final NumberCard blue9 = new NumberCard(Color.BLUE, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard yellow4 = new NumberCard(Color.YELLOW, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard yellow6 = new NumberCard(Color.YELLOW, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard green6 = new NumberCard( Color.GREEN, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard blue7 = new NumberCard(Color.BLUE, NumberEnum.SEVEN, ScoreValue.SEVEN);
    private static final NumberCard blue1 = new NumberCard(Color.BLUE, NumberEnum.ONE, ScoreValue.ONE);
    private static final Skip greenSkip = new Skip(Color.GREEN);
    private static final NumberCard red1 = new NumberCard(Color.RED, NumberEnum.ONE, ScoreValue.ONE);
    private static final NumberCard red9 = new NumberCard(Color.RED, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard blue0 = new NumberCard(Color.BLUE, NumberEnum.ZERO, ScoreValue.ZERO);
    private static final NumberCard green8 = new NumberCard(Color.GREEN, NumberEnum.EIGHT, ScoreValue.EIGHT);
    private static final NumberCard green2 = new NumberCard( Color.GREEN, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard red4 = new NumberCard(Color.RED, NumberEnum.FOUR, ScoreValue.FOUR);

    @BeforeEach
    public void initPlayer() {
        alice = new Player("Alice");
        bob = new Player("Bob");
        charles = new Player("Charles");

        alice.addCard(redSkip);
        alice.addCard(blue9);
        alice.addCard(yellow4);

        bob.addCard(yellow6);
        bob.addCard(green6);
        bob.addCard(blue7);

        charles.addCard(blue1);
        charles.addCard(greenSkip);
        charles.addCard(red1);
    }

    @BeforeEach
    public void initGame() {
        game.reset();
        game.addPlayer(alice, bob, charles);

        Discard discardPile = game.getDiscardPile();
        discardPile.addCard(red9);

        Deck deck = game.getDrawPile();
        deck.addCard(blue0);
        deck.addCard(green8);
        deck.addCard(green2);
        deck.addCard(red4);
        deck.addCard(green2);
    }

    @Test
    public void TestDeCoupsLegauxAvecDesCartesPasseTonTour() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(redSkip);
        alice.finishTurn();
        assertEquals(charles, game.getCurrentPlayer());
        assertEquals(redSkip, game.getDiscardPile().getTopCard());

        charles.playCard(greenSkip);
        charles.finishTurn();
        assertEquals(bob, game.getCurrentPlayer());
        assertEquals(greenSkip, game.getDiscardPile().getTopCard());

        bob.playCard(green6);
        bob.finishTurn();
        assertEquals(charles, game.getCurrentPlayer());
        assertEquals(green6, game.getDiscardPile().getTopCard());
    }

    @Test
    public void TestUneCarteSimpleIllegaleSurUnPasseTonTour() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(redSkip);
        alice.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        assertEquals(3,charles.getHand().size());
        assertThrows(PunishmentException.class, () -> charles.playCard(blue1));
        assertEquals(3,charles.getHand().size());
    }

    @Test
    public void TestUnPasseTonTourIllegalSurUneCarteSimple() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(blue9);
        alice.finishTurn();
        bob.playCard(blue7);
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        assertEquals(3,charles.getHand().size());
        assertThrows(PunishmentException.class, () -> charles.playCard(greenSkip));
        assertEquals(3,charles.getHand().size());
    }
}
