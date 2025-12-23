package TestUnitaireSupplementaire;

import application.model.card.NumberCard;
import application.model.card.special.Reverse;
import application.model.card.special.Wild;
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

public class TestCartesJoker {
    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    private static final Wild wild1 = new Wild();
    private static final NumberCard blue9 = new NumberCard(Color.BLUE, NumberEnum.NINE, ScoreValue.NINE);
    private static final NumberCard yellow4 = new NumberCard(Color.YELLOW, NumberEnum.FOUR, ScoreValue.FOUR);
    private static final NumberCard yellow6 = new NumberCard(Color.YELLOW, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard green6 = new NumberCard( Color.GREEN, NumberEnum.SIX, ScoreValue.SIX);
    private static final NumberCard blue7 = new NumberCard(Color.BLUE, NumberEnum.SEVEN, ScoreValue.SEVEN);
    private static final NumberCard blue1 = new NumberCard(Color.BLUE, NumberEnum.ONE, ScoreValue.ONE);
    private static final Wild wild2 = new Wild();
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

        alice.addCard(wild1);
        alice.addCard(blue9);
        alice.addCard(yellow4);

        bob.addCard(yellow6);
        bob.addCard(green6);
        bob.addCard(blue7);

        charles.addCard(blue1);
        charles.addCard(wild2);
        charles.addCard(red1);
    }

    @BeforeEach
    public void initGame() {
        game.reset();
        game.addPlayer(alice, bob, charles);

        wild1.setColor(Color.WILD);
        wild2.setColor(Color.WILD);

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
    public void TestDeCoupsLegauxAvecDesJoker() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(wild1);
        alice.chooseWildColor(Color.YELLOW);
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        bob.playCard(yellow6);
        bob.finishTurn();

        assertEquals(charles, game.getCurrentPlayer());
        charles.playCard(wild2);
        charles.chooseWildColor(Color.RED);
        charles.finishTurn();

        assertEquals(alice, game.getCurrentPlayer());
        alice.drawCard();
        alice.finishTurn();
    }

    @Test
    public void TestUneCarteSimpleIllegaleSurJoker() throws UnoException, PunishmentException {
        assertEquals(alice, game.getCurrentPlayer());
        alice.playCard(wild1);
        alice.chooseWildColor(Color.RED);
        alice.finishTurn();

        assertEquals(bob, game.getCurrentPlayer());
        assertEquals(3,bob.getHand().size());
        assertThrows(PunishmentException.class, () -> bob.playCard(blue7));
        assertEquals(3,bob.getHand().size());
    }

        // Changement du fonctionnement de la carte Joker
//    @Test
//    public void TestUnJokerSansChoisirLaCouleur() throws UnoException, PunishmentException {
//        assertEquals(alice, game.getCurrentPlayer());
//        alice.playCard(wild1);
//        assertThrows(UnoException.class, () -> alice.finishTurn());
//        assertEquals(2,alice.getHand().size());
//    }
}
