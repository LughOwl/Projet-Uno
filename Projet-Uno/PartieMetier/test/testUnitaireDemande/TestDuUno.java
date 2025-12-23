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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestDuUno {
    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    private static final NumberCard green2 = new NumberCard( Color.GREEN, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard yellow6 = new NumberCard(Color.YELLOW, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard blue2 = new NumberCard(Color.BLUE, NumberEnum.TWO, ScoreValue.TWO);
    private static final NumberCard yellow4 = new NumberCard(Color.YELLOW, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard blue9 = new NumberCard(Color.BLUE, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard blue7 = new NumberCard(Color.BLUE, NumberEnum.SEVEN, ScoreValue.SEVEN);
    private static final NumberCard green8 = new NumberCard(Color.GREEN, NumberEnum.EIGHT, ScoreValue.EIGHT);
    private static final NumberCard blue5 = new NumberCard(Color.BLUE, NumberEnum.FIVE, ScoreValue.FIVE);
    private static final NumberCard green0 = new NumberCard(Color.GREEN, NumberEnum.ZERO, ScoreValue.ZERO);
    private static final NumberCard blue3 = new NumberCard(Color.BLUE, NumberEnum.THREE, ScoreValue.THREE);

    @BeforeEach
    void  initPlayer(){
        alice = new Player("Alice");
        bob = new Player("Bob");
        charles = new Player("Charles");

        alice.addCard(green2);
        alice.addCard(yellow6);

        bob.addCard(blue2);
        bob.addCard(yellow4);

        charles.addCard(blue9);
        charles.addCard(blue7);
    }

    @BeforeEach
    void  initGame(){
        game.reset();
        game.addPlayer(alice, bob, charles);

        Discard discardPile = game.getDiscardPile();
        discardPile.addCard(green8);

        Deck deck = game.getDrawPile();
        deck.addCard(yellow6);
        deck.addCard(green2);
        deck.addCard(blue5);
        deck.addCard(green0);
        deck.addCard(blue3);
    }

    @Test
    void TestAliceDitUnoAuBonMoment() throws UnoException, PunishmentException {
        assertEquals(2, alice.getHand().size());
        alice.playCard(green2);
        alice.sayUno();
        alice.finishTurn();
        assertEquals(1, alice.getHand().size());
        assertEquals(green2, game.getDiscardPile().getTopCard());
        assertEquals(game.getCurrentPlayer(),bob);
    }

    @Test
    void TestAliceOublieDeDireUno() throws UnoException, PunishmentException {
        alice.playCard(green2);
        assertThrows(PunishmentException.class, () -> alice.finishTurn());
        alice.punishment();
        assertEquals(4, alice.getHand().size());
        assertEquals(green8, game.getDiscardPile().getTopCard());
        assertEquals(game.getCurrentPlayer(),bob);
    }

    @Test
    void TestBobDitUnoQuandPasSonTour() throws UnoException, PunishmentException {
        assertEquals(game.getCurrentPlayer(),alice);
        assertThrows(PunishmentException.class, () -> bob.sayUno());
        bob.punishment();
        assertEquals(4, bob.getHand().size());
        assertEquals(game.getCurrentPlayer(),alice);
        assertEquals(green8, game.getDiscardPile().getTopCard());
    }
}
