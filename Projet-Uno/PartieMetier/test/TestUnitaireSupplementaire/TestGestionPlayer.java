package TestUnitaireSupplementaire;

import application.model.game.Game;
import application.model.players.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestGestionPlayer {

    private static Player alice, bob, charles;
    private static Game game = Game.getInstance();

    @Test
    void addOnePlayerInGame() {
        game.reset();
        alice = new Player("Alice");
        game.addPlayer(alice);
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void removeOnePlayerInGame() {
        game.reset();
        alice = new Player("Alice");
        game.addPlayer(alice);
        game.removePlayer(alice);
        assertEquals(0, game.getPlayers().size());
    }

    @Test
    void duplicatePlayerInGame() {
        game.reset();
        alice = new Player("Alice");
        game.addPlayer(alice);
        assertThrows(IllegalArgumentException.class, () -> game.addPlayer(alice));
    }
}
