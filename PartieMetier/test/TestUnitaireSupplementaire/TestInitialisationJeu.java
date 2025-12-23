package TestUnitaireSupplementaire;

import application.model.game.Game;
import application.model.players.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInitialisationJeu {

    private static final Player alice = new Player("Alice");
    private static final Player bob = new Player("Bob");
    private static final Player charles = new Player("Charles");

    private static Game game = Game.getInstance();

    @Test
    public void TestNombreCarteChaqueJoueurEtGame(){
        game.reset();
        game.addPlayer(alice,bob,charles);
        game.startGame();
        assertEquals(7,alice.getHand().size());
        assertEquals(7,bob.getHand().size());
        assertEquals(7,charles.getHand().size());
        assertEquals(1,game.getDiscardPile().size());
        assertEquals(86,game.getDrawPile().size());
    }
}
