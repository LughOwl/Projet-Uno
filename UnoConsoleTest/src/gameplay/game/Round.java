package gameplay.game;

public class Round {
    private int roundNumber;
    private int score;
    private int winnerIndex;

    public Round(int roundNumber) {
        setRoundNumber(roundNumber);
        setScore(0);
        setWinnerIndex(-1);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        if (roundNumber < 0) {
            throw new IllegalArgumentException("Round number cannot be negative");
        }
        this.roundNumber = roundNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Round score cannot be negative");
        }
        this.score = score;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public void setWinnerIndex(int winnerIndex) {
        if (winnerIndex < -1) {
            throw new IllegalArgumentException("Round winner index cannot be negative");
        }
        this.winnerIndex = winnerIndex;
    }
}
