package application.model.game;


import application.model.players.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreBoard {
    private HashMap<Player, Integer> scorePlayers = new HashMap<>();
    private int winningScore = 500;

    public ScoreBoard() {}

    public ScoreBoard(ArrayList<Player> players) {
        addPlayers(players);
    }


    //*********************//
    // Getters and setters //
    //*********************//

    public HashMap<Player, Integer> getScorePlayers() {
        return scorePlayers;
    }

    public int getScorePlayer(Player player) {
        return scorePlayers.get(player);
    }

    public void setScorePlayers(HashMap<Player, Integer> scorePlayers) {
        this.scorePlayers = scorePlayers;
    }
    public void setScorePlayers(Player player, int scorePlayers) {
        this.scorePlayers.put(player, scorePlayers);
    }

    public int getWinningScore() {
        return winningScore;
    }

    public void setWinningScore(int winningScore) {
        this.winningScore = winningScore;
    }

    public void addPlayer(Player player) {
        scorePlayers.put(player, 0);
    }

    public void addPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            addPlayer(p);
        }
    }

    public void addScorePlayer(Player player, int score) {
        scorePlayers.put(player, getScorePlayers().get(player) + score);
    }

    public void removePlayer(Player player) {
        scorePlayers.remove(player);
    }

    //****************************//
    // End of getters and setters //
    //****************************//

    /**
     * This method checks if a player has reached the winning score.
     * @return true if a player has reached the winning score, false otherwise.
     */
    public Player getWinner() {
        for (Player player : scorePlayers.keySet()) {
            if (scorePlayers.get(player) >= winningScore) {
                return player;
            }
        }
        return null;
    }

    /**
     * This method checks if a player has reached the winning score.
     * @return true if a player has reached the winning score, false otherwise.
     */
    public void removePlayers(List<Player> list) {
        for (Player player : list) {
            scorePlayers.remove(player);
        }
    }
}
