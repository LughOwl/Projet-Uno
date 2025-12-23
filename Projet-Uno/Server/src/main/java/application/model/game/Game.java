package application.model.game;

import application.model.card.Card;
import application.model.card.special.Wild;
import application.model.card.special.draw.WildDrawFour;
import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;
import application.model.exception.PunishmentException;
import application.model.exception.UnoException;
import application.model.players.Player;
import application.model.utils.Utils;
import jdbc.DaoJoueur;
import jdbc.DaoParticipe;
import jdbc.DaoPartie;

import java.util.*;

public final class Game {
    private static Game instance;
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    private boolean currentPlayerMustPlay = true;

    private int playDirection = 1;
    private int varSkip = 1;

    private Discard discardPile;
    private Deck drawPile;
    private ScoreBoard scoreBoard;

    private int accumulatedDraw = 0;
    private Color currentColor;


    // BDD
    private int idPartie;

    /*
    public Game(ArrayList<Player> players) {
        setPlayers(players);
        setDiscardPile(new Discard());
        setDrawPile(new Deck());
        setScoreBoard(new ScoreBoard(players));
    }

    public Game() {
        setDiscardPile(new Discard());
        setDrawPile(new Deck());
        setScoreBoard(new ScoreBoard());
    }
    */

    private Game() {
        setDiscardPile(new Discard());
        setDrawPile(new Deck());
        setScoreBoard(new ScoreBoard());
    }

    public static Game getInstance(){
        if (instance == null){
            instance = new Game();
        }
        return instance;
    }

    public void reset(){
        currentPlayerIndex = 0;
        currentPlayerMustPlay = true;
        playDirection = 1;
        accumulatedDraw = 0;
        players = new ArrayList<>();
        setDiscardPile(new Discard());
        setDrawPile(new Deck());
        setScoreBoard(new ScoreBoard());
    }

    //*********************//
    // Getters and setters //
    //*********************//

    public ArrayList<Player> getPlayers() {
        return Game.getInstance().players;
    }

    public Player getPlayer(String pseudo){
        for (Player p : Game.getInstance().players){
            if (p.getName().equals(pseudo)){
                return p;
            }

        }
        throw new IllegalArgumentException("Player " + pseudo + " not found");

    }

    public void setPlayers(ArrayList<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null or empty");
        }
        this.players = players;
    }

    public void addPlayer(Player... player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (Arrays.stream(player).anyMatch(p -> players.contains(p))) {
            throw new IllegalArgumentException("Player already exists");
        }
        players.addAll(Arrays.asList(player));
        scoreBoard.addPlayers(new ArrayList<>(List.of(player)));
    }

    public void removePlayer(Player... player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (Arrays.stream(player).anyMatch(p -> !players.contains(p))) {
            throw new IllegalArgumentException("Player does not exist");
        }
        players.removeAll(Arrays.asList(player));
        scoreBoard.removePlayers(Arrays.asList(player));
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayerIndex(int value) {
        this.currentPlayerIndex = (value + this.getPlayers().size()) % this.getPlayers().size();
    }

    public void setCurrentPlayer(Player player) {
        currentPlayerIndex = players.indexOf(player);
    }

    public boolean isCurrentPlayerMustPlay() {
        return currentPlayerMustPlay;
    }

    public void setCurrentPlayerMustPlay(boolean value) {
        this.currentPlayerMustPlay = value;
    }

    public int getPlayDirection() {
        return playDirection;
    }

    public void setPlayDirection(int value) {
        this.playDirection = value;
    }

    public void changeDirection() {
        this.playDirection *= -1;
    }

    public int getVarSkip() {
        return varSkip;
    }

    public void setVarSkip(int varSkip) {
        this.varSkip = varSkip;
    }

    public Discard getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(Discard discardPile) {
        if (discardPile == null) {
            throw new IllegalArgumentException("Discard pile cannot be null");
        }
        this.discardPile = discardPile;
    }

    public Deck getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(Deck drawPile) {
        if (drawPile == null) {
            throw new IllegalArgumentException("Draw pile cannot be null");
        }
        this.drawPile = drawPile;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public int getAccumulatedDraw() {
        return accumulatedDraw;
    }

    public void setAccumulatedDraw(int accumulatedDraw) {
        this.accumulatedDraw = accumulatedDraw;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        Game.getInstance().currentColor = currentColor;
    }


    //****************************//
    // Méthodes inutiles ici ?    //
    //****************************//

    /**
     * Adds specific cards to the main draw pile.
     * <br><br>
     * This method fills the draw pile by adding one or more specified cards.
     * The cards are provided as variable arguments, allowing flexibility in
     * the number of cards passed.
     *
     * @param card One or more cards to add to the draw pile
     */
    /*
    public void refillDrawPile(Card... card) {
        for (Card c : card) {
            getDrawPile().addCard(c);
        }
    }
    */

    /**
     * Allows the current player to declare "Uno" when they have only one card left.
     * <br><br>
     * This method checks if the player is the current player and has only one card in their hand.
     * If these conditions are met, it sets the player's Uno validation status to true and
     * updates the game state accordingly. If not, a PunishmentException is thrown.
     *
     * @param player The player declaring "Uno"
     * @throws PunishmentException If the player cannot declare "Uno"
     */
    /*
    public void sayUno(Player player) throws PunishmentException {
        if(!getCurrentPlayer().equals(player) || player.getHand().size() != 1) {
            throw new PunishmentException("You can't say Uno");
        }else{
            player.setValidationUno(true);
            setCurrentPlayerMustPlay(false);
        }
    }
    */



    /**
     * Allows the current player to choose a new color for a Wild card.
     * <br><br>
     * This method validates that the top card on the discard pile is a Wild card
     * before changing its color to the specified one. If the top card is not a Wild card,
     * a PunishmentException is thrown.
     *
     * @param color The chosen color to set for the Wild card
     * @throws PunishmentException If the top card is not a Wild card
     */
    /*
    public void chooseWildColor(Color color) throws PunishmentException {
        if (discardPile.getTopCard().getColor() != Color.WILD) {
            throw new PunishmentException("You can't change the color!");
        }
        discardPile.getTopCard().setColor(color);
    }
    */

    /**
     * Allows the current player to draw a card from the draw pile.
     * <br><br>
     * This method checks if the player is the current player and if they are required
     * to play. If these conditions are met, the player draws a card from the draw pile
     * and adds it to their hand. The "must play" status is then reset for the current player.
     *
     * @param p The player who is drawing a card
     * @throws UnoException       If the player is not allowed to draw a card
     * @throws PunishmentException If the player cannot draw a card at this time
     */
    /*
    public void drawCard(Player p) throws UnoException, PunishmentException {
        if (p != getCurrentPlayer() || !isCurrentPlayerMustPlay()) {
            throw new PunishmentException("You can't play");
        }
        if (accumulatedDraw > 0) {
            throw new PunishmentException("You must take the damage or play a SpecialDrawCard");
        }
        p.addCard(removeCardDeck());
        setCurrentPlayerMustPlay(false);
    }
    */

    /**
     * Applies a punishment to the specified player by drawing two cards from the draw pile.
     * <br><br>
     * This method is typically called when a player violates game rules or conditions.
     * The player draws two cards from the draw pile, and if they are the current player,
     * their "must play" status is reset, and the game proceeds to the next player.
     *
     * @param p The player to be punished
     * @throws UnoException       If an error occurs during the punishment process
     * @throws PunishmentException If the player cannot be punished or draw cards
     */
    /*
    public void punishment(Player p) throws UnoException, PunishmentException {
        p.addCard(removeCardDeck());
        p.addCard(removeCardDeck());

        if (p == getCurrentPlayer()) {
            setCurrentPlayerMustPlay(false);
            nextPlayer();
        }
    }
    */

    /**
     * Allows a player to play a card from their hand, applying its effects and updating the game state.
     * <br><br>
     * This method performs several checks to ensure the action is valid:
     * - Verifies that the player has the specified card in their hand.
     * - Checks if the card is a valid move based on the current game state.
     * - Validates that it is the player's turn to play.
     * <br><br>
     * If all checks pass, the card's effect is applied, and the card is added to the discard pile.
     * The player's hand is updated accordingly, and the game state is adjusted for the next turn.
     *
     * @param p    The player attempting to play the card
     * @param card The card being played
     * @throws UnoException       If any of the checks fail or if an error occurs during gameplay
     * @throws PunishmentException If the move is not allowed according to game rules
     */
    /*
    public void playCard(Player p, Card card) throws UnoException, PunishmentException {
        checkPlayerHasTheCard(p, card);
        checkIsValidMove(card);
        checkValidatePlayerTurn(p);
        card.playEffect(this);
        handleDrawStacking(card);
        resestWildCard(discardPile.getTopCard());

        p.removeCard(card);
        discardPile.addCard(card);
        setCurrentPlayerMustPlay(false);

        hasWinRound(p);
    }
    */

    /**
     * Checks if the specified player has the specified card in their hand.
     * <br><br>
     * This method verifies that the player possesses the card they are attempting to play.
     * If the player does not have the card, an UnoException is thrown to indicate the error.
     *
     * @param p The player whose hand is being checked
     * @param c The card being checked for in the player's hand
     * @throws UnoException If the player does not have the specified card
     */
    /*
    private void checkPlayerHasTheCard(Player p, Card c) throws UnoException {
        if (!p.getHand().contains(c)) throw new UnoException("Card not in hand");
    }
    */

    /**
     * Validates whether the specified card is a valid move based on the game rules.
     * <br><br>
     * This method checks if the card can be played on the discard pile by:
     * - Ensuring the card matches the top card's color or type.
     * - Enforcing rules for special cards, such as DrawTwo and WildDrawFour.
     * <br><br>
     * If the card is not a valid move, a PunishmentException is thrown to indicate
     * that the player cannot play that card at this time.
     *
     * @param card The card being played
     * @throws PunishmentException If the card is not a valid move
     */
    /*
    private void checkIsValidMove(Card card) throws PunishmentException {
        if (!discardPile.isValidMove(card)) throw new PunishmentException("You can't discard this card");
        if (accumulatedDraw>0){
            if (card.getType() != Type.DRAW_TWO && card.getType() != Type.WILD_DRAW_FOUR){
                throw new PunishmentException("You must play a draw card or take the damage");
            }
            if (discardPile.getTopCard().getType() == Type.WILD_DRAW_FOUR && card.getType() != Type.WILD_DRAW_FOUR) {
                throw new PunishmentException("You must play a WildDrawFour card or take the damage");
            }
        }
    }
    */

    /**
     * Validates whether the specified player is allowed to play their turn.
     * <br><br>
     * This method checks if the player is the current player and if they are required
     * to play. If either condition is not met, a PunishmentException is thrown to indicate
     * that the player cannot take their turn at this time.
     *
     * @param p The player attempting to play their turn
     * @throws PunishmentException If the player is not allowed to play
     */
    /*
    private void checkValidatePlayerTurn(Player p) throws PunishmentException {
        if (p != getCurrentPlayer()) {
            throw new PunishmentException("It's not your turn to play");
        }
        if (!isCurrentPlayerMustPlay()) {
            throw new PunishmentException("You already played your turn");
        }
    }
    */

    /**
     * Moves to the next player in the game, ensuring that the current player has completed their turn.
     * <br><br>
     * This method checks if the round has ended, verifies if the current player has declared "Uno,"
     * and ensures that the player is required to play. If all conditions are met, it moves to the
     * next player and sets their "must play" status accordingly.
     *
     * @throws UnoException       If the round is already over or if the player has not won
     * @throws PunishmentException If the current player has violated any rules
     */
    /*
    public void nextPlayer() throws UnoException, PunishmentException {
        checkRoundEnd();
        checkUnoViolation();
        checkPlayerMustPlay();
        checkWildColorSet();

        moveToNextPlayer();
        setCurrentPlayerMustPlay(true);
    }
    */

    /**
     * Checks if the current player has won the round by having an empty hand.
     * <br><br>
     * This method verifies if the current player's hand is empty, indicating they have
     * successfully played all their cards. If the player has won, it updates the game
     * state accordingly and prepares for the next round.
     *
     * @throws UnoException If the round is already over or if the player has not won
     */
    /*
    private void checkRoundEnd() throws UnoException {
        if (getCurrentPlayer().getHand().isEmpty()) {
            throw new UnoException("The roundcurrentPlayerMustPlay is end");
        }
    }
    */

    /**
     * Checks if the current player has violated the Uno rule by not declaring "Uno."
     * <br><br>
     * This method verifies if the current player has only one card left in their hand
     * and has not declared "Uno." If they fail to declare, they are penalized by drawing
     * a card from the discard pile, and the game state is updated accordingly.
     *
     * @throws PunishmentException If the current player fails to declare "Uno"
     * @throws UnoException If the game state is invalid
     */
    /*
    private void checkUnoViolation() throws PunishmentException, UnoException {
        Player current = getCurrentPlayer();
        if (current.getHand().size() == 1 && !current.isUno()) {
            current.addCard(discardPile.getTopCard());
            discardPile.removeTopCard();
            getCurrentPlayer().setValidationUno(false);
            throw new PunishmentException("The current player don't say uno");
        }
    }
    */

    /**
     * Checks if the current player is required to play a card.
     * <br><br>
     * This method verifies if the current player must play a card based on the game rules.
     * If they are required to play and fail to do so, an exception is thrown to indicate
     * that they must take their turn.
     *
     * @throws UnoException If the current player must play a card
     */
    /*
    private void checkPlayerMustPlay() throws UnoException {
        if(isCurrentPlayerMustPlay()) {
            throw new UnoException("You must play !");
        }
    }
    */

    /**
     * Checks if the top card on the discard pile is a Wild card and requires a color choice.
     * <br><br>
     * This method ensures that if the top card is a Wild card, the player must choose a color
     * before proceeding. If they fail to do so, an exception is thrown to indicate that a color
     * must be selected.
     *
     * @throws UnoException If the top card is a Wild card and no color has been chosen
     */
    /*
    private void checkWildColorSet() throws UnoException {
        if(discardPile.getTopCard().getColor() == Color.WILD) {
            throw new UnoException("You must chose a color!");
        }
    }
    */

    /**
     * Allows the current player to take the accumulated draw cards from the discard pile.
     * <br><br>
     * This method checks if the top card on the discard pile is a Draw card. If it is, the
     * player draws the specified number of cards from the draw pile and resets the accumulated
     * draw count. The game then proceeds to the next player.
     *
     * @throws UnoException If the top card is not a Draw card
     * @throws PunishmentException If the player cannot take the draw cards
     */
    /*
    public void takeDrawCard() throws UnoException, PunishmentException {
        if (!(discardPile.getTopCard() instanceof Draw)) throw new UnoException("Top card not compliant, impossible to recover the draws");

        for (int i = 0; i < accumulatedDraw; i++) {
            getCurrentPlayer().addCard(removeCardDeck());
        }
        accumulatedDraw = 0;
        setCurrentPlayerMustPlay(false);
        nextPlayer();
    }
    */

    /**
     * Handles the stacking of draw effects based on the played card.
     * <br><br>
     * This method accumulates the draw amount if the played card is a Draw card.
     * If a non-Draw card is played, it resets the accumulated draw to zero.
     *
     * @param card The card being played
     */
    /*
    public void handleDrawStacking(Card card) {
        if (card instanceof Draw) {
            accumulatedDraw += ((Draw) card).getDrawAmount();
        }
    }
    */

    //*********************************//
    //          Game Action            //
    //*********************************//

    /**
     * Moves to the next player in the game, considering the current play direction.
     * <br><br>
     * This method updates the current player index based on the play direction (forward or backward).
     * It ensures that the index wraps around correctly when reaching the end of the player list.
     *
     */
    public void moveToNextPlayer()  {
        currentPlayerIndex = (currentPlayerIndex + playDirection * varSkip + players.size()) % players.size();
        setVarSkip(1);
    }

    //*********************************//
    //         Cards Action            //
    //*********************************//

    /**
     * Removes and returns the top card from the draw pile, refilling the pile if it is empty.
     * <br><br>
     * This method ensures that the draw pile is not empty before attempting to draw a card.
     * If the draw pile is empty, it calls the `refillDeck` method to replenish it.
     * The top card of the draw pile is then drawn and returned.
     *
     * @return The top card from the draw pile
     * @throws UnoException If an error occurs during the refill process or card retrieval
     */
    public Card removeCardDeck() throws UnoException {
        if (this.drawPile.isEmpty()) {
            refillDeck();
        }
        return this.drawPile.drawCard();
    }


    /**
     * Refills the draw pile by shuffling the discard pile and moving its cards back to the draw pile.
     * <br><br>
     * This method checks if the discard pile has enough cards to refill the draw pile.
     * If there are too few cards, an UnoException is thrown. The top card of the discard
     * pile is preserved, and the rest of the cards are shuffled and added to the draw pile.
     *
     * @throws UnoException If there are not enough cards in the discard pile to refill
     */
    public void refillDeck() throws UnoException {
        if (discardPile.size() <= 1) {
            throw new UnoException("Crash game, no more card to refill");
        }
        Color currentColor = this.currentColor;
        Card topCard = discardPile.getTopCard();
        discardPile.removeTopCard();

        ArrayList<Card> cardsToShuffle = new ArrayList<>(discardPile.getDiscardPile());
        Collections.shuffle(cardsToShuffle);
        drawPile.addAll(cardsToShuffle);

        discardPile.clear();
        discardPile.addCard(topCard);
        setCurrentColor(currentColor);
    }


    /**
     * Moves all cards from the specified player's hand to the draw pile.
     * <br><br>
     * This method iterates through the player's hand and adds each card to the draw pile,
     * effectively clearing the player's hand. It ensures that the game state is updated
     * correctly by removing the cards from the player's hand.
     *
     * @param player The player whose hand is being moved to the draw pile
     */
    public void moveHandPlayerToDrawPile(Player player) {
        for (Card card : player.getHand()) {
            drawPile.addCard(card);
            player.removeCard(card);
        }
    }

    //*******************************************************//
    //                      Gestion Score                    //
    //*******************************************************//

    /**
     * Retrieves the score of a specific player by summing the scores of their cards.
     * <br><br>
     * This method iterates through the player's hand and calculates the total score
     * based on the individual card scores. The final score is returned as an integer.
     *
     * @param player The player whose score is to be calculated
     * @return The total score of the specified player
     */
    public int getScorePlayer(Player player) {
        int score = 0;
        for (Card card : player.getHand()) {
            score += card.getScore();
        }
        return score;
//        return getScoreBoard().getScorePlayer(player);
    }


    public int getScoreWinner(Player player) {
        return getScoreBoard().getScorePlayer(player);
    }
    /**
     * Adds a specified score to a player's total score.
     * <br><br>
     * This method updates the player's score by adding the provided value to their
     * existing score. It ensures that the score is updated correctly in the game's
     * scoring system.
     *
     * @param player The player whose score is to be updated
     * @param score  The score to be added to the player's total
     */
    public void addScorePlayer(Player player, int score) {
        getScoreBoard().addScorePlayer(player, score);
    }

    /**
     * Updates the score of a specific player by summing the scores of all other players.
     * <br><br>
     * This method calculates the total score of all players except the specified one
     * and adds it to the specified player's score. It ensures that the scoring system
     * reflects the current game state accurately.
     *
     * @param player The player whose score is to be updated
     */
    public void updateScorePlayer(Player player){
        int score = 0;
        for (Player p : players) {
            if (p.equals(player)) continue;
            score =+ getScorePlayer(p);
        }
        addScorePlayer(player, score);
    }

    /**
     * Updates the scores of all players in the game.
     * <br><br>
     * This method iterates through the list of players and updates each player's score
     * by calling the `updateScorePlayer` method. It ensures that all players' scores
     * are accurately reflected in the game's scoring system.
     */
    private void updateScore() {
        for (Player player : players) {
            updateScorePlayer(player);
//            DaoParticipe.enregistrerParticipation(DaoJoueur.getIdPlayer(player.getName()), idPartie, getScorePlayer(player));
        }


        // mise à jour de leur score en bdd
    }
    public void setScore(Player player, int score) {
        getScoreBoard().setScorePlayers(player, score);
        for (Card card : player.getHand()) {
            card.setScore(ScoreValue.fromValue(score));
        }
//        DaoParticipe.enregistrerParticipation(DaoJoueur.getIdPlayer(player.getName()), idPartie, score);
    }

    public void setScoreForAllPlayer(int score) {
        for (Player player : players) {
            getScoreBoard().setScorePlayers(player, score);
            for (Card card : player.getHand()) {
                card.setScore(ScoreValue.ZERO);
            }
//            DaoParticipe.enregistrerParticipation(DaoJoueur.getIdPlayer(player.getName()), idPartie, score);
        }

    }


    //*******************************************************//
    //               Gestion Début Partie/round              //
    //*******************************************************//


    /**
     * Initializes and starts the game by setting up players, the draw pile, and the discard pile.
     * <br><br>
     * This method shuffles the player order, creates the necessary cards for the game,
     * and distributes 7 cards to each player from the draw pile. Finally, it places
     * one card from the draw pile into the discard pile to begin the game.
     */
    public void startGame() {
        Collections.shuffle(players);

        Utils.createCards(this);

        getDrawPile().shuffle();
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCard(getDrawPile().drawCard());
            }
        }

        getDiscardPile().addCard(getDrawPile().drawCard());
        while (getDiscardPile().getTopCard() instanceof Wild ||
               getDiscardPile().getTopCard() instanceof WildDrawFour) {
            getDrawPile().addCard(getDiscardPile().getTopCard());
            try {
                getDiscardPile().removeTopCard();
            } catch (UnoException _) {}
            getDiscardPile().addCard(getDrawPile().drawCard());


        }

        // Genereted id in BDD
        idPartie = DaoPartie.creerPartie();

    }


    /**
     * Restarts the round by resetting the game state and redistributing cards to players.
     * <br><br>
     * This method clears the discard pile, shuffles the draw pile, and redistributes
     * 7 cards to each player. It also sets the play direction and checks if any player
     * has reached the winning score.
     *
     * @throws UnoException If the game has already ended or if an error occurs during the process
     */
    public void restartRound() throws UnoException {
        if (!checkGameEnd()) throw new UnoException("Game not end");
        resetDecks();
        setPlayDirection(1);
        distributeCards();

        discardPile.addCard(drawPile.drawCard());
    }

    /**
     * Checks if any player has reached the winning score and throws an exception if so.
     * <br><br>
     * This method iterates through all players and checks their scores against the
     * winning score defined in the scoreboard. If a player has reached or exceeded
     * the winning score, an UnoException is thrown to indicate that the game has ended.
     *
     * @throws UnoException If a player has reached the winning score
     */
    private boolean checkGameEnd() throws UnoException {
        for (Player player : players) {
            if (scoreBoard.getScorePlayer(player) >= scoreBoard.getWinningScore()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resets the decks by moving all cards from players' hands to the draw pile,
     * shuffling the draw pile, and clearing the discard pile.
     * <br><br>
     * This method prepares the game for a new round by ensuring that all cards are
     * available for play and that the game state is reset to its initial conditions.
     */
    private void resetDecks() {
        for (Player player : players) {
            moveHandPlayerToDrawPile(player);
        }
        drawPile.addAll(discardPile.getDiscardPile());
        discardPile.clear();
        drawPile.shuffle();
    }

    /**
     * Distributes 7 cards to each player from the draw pile.
     * <br><br>
     * This method iterates through the list of players and adds 7 cards to each player's hand
     * from the draw pile. It ensures that each player starts with a full hand of cards for gameplay.
     */
    private void distributeCards() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCard(drawPile.drawCard());
            }
        }
    }

    //*******************************************************//
    //                 Gestion Fin Partie/round              //
    //*******************************************************//


//    /**
//     * Checks if the specified player has won the round by having an empty hand.
//     * <br><br>
//     * This method verifies if the player's hand is empty, indicating they have successfully
//     * played all their cards. If the player has won, the game state is updated accordingly.
//     *
//     * @param p The player to check for a win
//     * @return true if the player has won the round, false otherwise
//     */
//    public boolean isWinRound(Player p) {
//        return p.getHand().isEmpty();
//    }

//    /**
//     * Checks if the specified player has won the round and updates the game state if so.
//     * <br><br>
//     * This method verifies if the player has won the round by checking if their hand is empty.
//     * If they have won, it calls the `endRound` method to update the game state accordingly.
//     *
//     * @param p The player to check for a win
//     * @throws UnoException If an error occurs during the round end process
//     */
//    public void hasWinRound(Player p) throws UnoException {
//        if (!isWinRound(p)) return;
//        endRound();
//    }

//    /**
//     * Ends the current round by updating the scores of all players.
//     * <br><br>
//     * This method is called when a player wins the round. It updates the scores of all players
//     * based on their performance in the round and prepares for the next round of gameplay.
//     *
//     * @throws UnoException If an error occurs during the round end process
//     */
//    public void endRound() throws UnoException {
//        updateScore();
//    }

    /**
     * Ends the game by clearing all players' hands and resetting the draw and discard piles.
     * <br><br>
     * This method is called when the game is over, ensuring that all game state is reset
     * and ready for a new game or round. It clears the players' hands and resets the
     * draw and discard piles to their initial states.
     */
    public void endGame(Player winner) {
//        updateScore();
        DaoParticipe.enregistrerParticipation(DaoJoueur.getIdPlayer(winner.getName()), idPartie, getScoreWinner(winner));

        for (Player player : players) {
            if (!player.equals(winner)) {
                DaoParticipe.enregistrerParticipation(DaoJoueur.getIdPlayer(player.getName()), idPartie, 0);
            }
            player.getHand().clear();
        }
        discardPile.clear();
        drawPile.clear();
    }

    public void endGameScore0() {
        setScoreForAllPlayer(0);

        for (Player player : players) {
            player.getHand().clear();
        }
        discardPile.clear();
        drawPile.clear();

    }

    public Player getPlayerByName(String username) {
        for (Player player : players) {
            if (player.getName().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public ArrayList<Player> getPlayerCurrentToLast() {
        ArrayList<Player> playersToReturn = new ArrayList<>();
        if (players.isEmpty()) {
            return playersToReturn;
        }
        int index = currentPlayerIndex;
        do {
            playersToReturn.add(players.get(index));
            index = (index + playDirection + players.size()) % players.size();
        } while (index != currentPlayerIndex);
        return playersToReturn;
    }

    public String getStringPlayerCurrentToLast() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Player> playersToReturn = getPlayerCurrentToLast();
        for (Player player : playersToReturn) {
            sb.append(player.getName()).append(" ");
        }
        return sb.toString().trim();
    }

//    public void nextPlayer() {
//            moveToNextPlayer();
//            players.get(currentPlayerIndex).addCard(removeCardDeck());
//
//    }

    public Player getWinner() {
        Player winner = null;
        for (Player player : players) {
            if (player.getHand().isEmpty())
                winner = player;
        }
        return winner;
    }
}