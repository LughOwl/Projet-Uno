package application.model.players;

import application.model.card.Card;
import application.model.card.special.draw.Draw;
import application.model.enumUno.Color;
import application.model.exception.EndGameException;
import application.model.exception.PunishmentException;
import application.model.exception.UnoException;
import application.model.game.Game;

import java.util.ArrayList;

public class Player {

    private String name;
    private final ArrayList<Card> hand = new ArrayList<>();
    private Boolean unoValidation = false;
    

    public Player(String name) {
        setName(name);
    }


    //*********************//
    // Getters and setters //
    //*********************//

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.add(card);
    }

    public void removeCard(Card card) {
        if (card == null || !hand.contains(card)) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.remove(card);
    }

    public boolean isUno() {
        return this.unoValidation;
    }

    public void setValidationUno(boolean value) {
        this.unoValidation = value;
    }


    //***********************************//
    //          Player Action            //
    //***********************************//

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
     * @param card The card being played
     * @throws UnoException       If any of the checks fail or if an error occurs during gameplay
     * @throws PunishmentException If the move is not allowed according to game rules
     */
    public void playCard(Card card) throws UnoException, PunishmentException {
        checkValidatePlayerTurn();
        checkPlayerHasTheCard(card);
        checkIsValidMove(card);
        card.playEffect();

        this.removeCard(card);
        Game.getInstance().getDiscardPile().addCard(card);
        Game.getInstance().setCurrentPlayerMustPlay(false);
    }

    /**
     * Allows the current player to draw a card from the draw pile.
     * <br><br>
     * This method checks if the player is the current player and if they are required
     * to play. If these conditions are met, the player draws a card from the draw pile
     * and adds it to their hand. The "must play" status is then reset for the current player.
     *
     * @throws UnoException       If the player is not allowed to draw a card
     * @throws PunishmentException If the player cannot draw a card at this time
     */
    public void drawCard() throws UnoException, PunishmentException {
        checkValidatePlayerTurn();
        if (Game.getInstance().getAccumulatedDraw() > 0) {
            throw new PunishmentException("You must take the damage or play a SpecialDrawCard");
        }
        this.addCard(Game.getInstance().removeCardDeck());
        Game.getInstance().setCurrentPlayerMustPlay(false);
    }

    /**
     * Allows the current player to declare "Uno" when they have only one card left.
     * <br><br>
     * This method checks if the player is the current player and has only one card in their hand.
     * If these conditions are met, it sets the player's Uno validation status to true and
     * updates the game state accordingly. If not, a PunishmentException is thrown.
     *
     * @throws PunishmentException If the player cannot declare "Uno"
     */
    public void sayUno() throws PunishmentException {
        if(!Game.getInstance().getCurrentPlayer().equals(this) || getHand().size() != 1) {
            throw new PunishmentException("You can't say Uno");
        }
        this.setValidationUno(true);
    }

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
    public void chooseWildColor(Color color) throws PunishmentException {
        if (this != Game.getInstance().getCurrentPlayer()) {
            throw new PunishmentException("It's not your turn to play");
        }
        if (Game.getInstance().getDiscardPile().getTopCard().getColor() != Color.WILD) {
            throw new PunishmentException("You can't change the color!");
        }
        Game.getInstance().setCurrentColor(color);
    }

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
    public void finishTurn() throws UnoException, PunishmentException, EndGameException {
        checkRoundEnd();
        checkUnoViolation();
        checkPlayerMustPlay();
        checkWildColorSet();

        Game.getInstance().moveToNextPlayer();
        Game.getInstance().setCurrentPlayerMustPlay(true);
    }

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
    public void takeDrawCard() throws UnoException, PunishmentException, EndGameException {
        if (!(Game.getInstance().getDiscardPile().getTopCard() instanceof Draw)) throw new UnoException("Top card not compliant, impossible to recover the draws");

        for (int i = 0; i < Game.getInstance().getAccumulatedDraw(); i++) {
            Game.getInstance().getCurrentPlayer().addCard(Game.getInstance().removeCardDeck());
        }
        Game.getInstance().setAccumulatedDraw(0);
        Game.getInstance().setCurrentPlayerMustPlay(false);
        finishTurn();
    }

    /**
     * Applies a punishment to the specified player by drawing two cards from the draw pile.
     * <br><br>
     * This method is typically called when a player violates game rules or conditions.
     * The player draws two cards from the draw pile, and if they are the current player,
     * their "must play" status is reset, and the game proceeds to the next player.
     *
     * @throws UnoException       If an error occurs during the punishment process
     * @throws PunishmentException If the player cannot be punished or draw cards
     */
    public void punishment() throws UnoException, PunishmentException, EndGameException {
        this.addCard(Game.getInstance().removeCardDeck());
        this.addCard(Game.getInstance().removeCardDeck());

        if (this == Game.getInstance().getCurrentPlayer()) {
            Game.getInstance().setCurrentPlayerMustPlay(false);
            finishTurn();
        }
    }

    //*******************************************************//
    //                      Gestion Verifications            //
    //*******************************************************//

    /**
     * Checks if the specified player has the specified card in their hand.
     * <br><br>
     * This method verifies that the player possesses the card they are attempting to play.
     * If the player does not have the card, an UnoException is thrown to indicate the error.
     *
     * @param c The card being checked for in the player's hand
     * @throws UnoException If the player does not have the specified card
     */
    private void checkPlayerHasTheCard(Card c) throws UnoException {
        if (!getHand().contains(c)) throw new UnoException("Card not in hand");
    }


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
    private void checkIsValidMove(Card card) throws PunishmentException, UnoException {
        if (!card.canPlay()) throw new PunishmentException("You can't discard this card");
    }

    /**
     * Validates whether the specified player is allowed to play their turn.
     * <br><br>
     * This method checks if the player is the current player and if they are required
     * to play. If either condition is not met, a PunishmentException is thrown to indicate
     * that the player cannot take their turn at this time.
     *
     * @throws PunishmentException If the player is not allowed to play
     */
    private void checkValidatePlayerTurn() throws PunishmentException {
        if (this != Game.getInstance().getCurrentPlayer()) {
            throw new PunishmentException("It's not your turn to play");
        }
        if (!Game.getInstance().isCurrentPlayerMustPlay()) {
            throw new PunishmentException("You already played your turn");
        }
    }

    /**
     * Checks if the current player has won the round by having an empty hand.
     * <br><br>
     * This method verifies if the current player's hand is empty, indicating they have
     * successfully played all their cards. If the player has won, it updates the game
     * state accordingly and prepares for the next round.
     *
     * @throws UnoException If the round is already over or if the player has not won
     */
    private void checkRoundEnd() throws EndGameException{
        if (Game.getInstance().getCurrentPlayer().getHand().isEmpty()) {
            throw new EndGameException("The round is end");
        }
    }

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
    private void checkUnoViolation() throws PunishmentException, UnoException {
        Player current = Game.getInstance().getCurrentPlayer();
        if (current.getHand().size() == 1 && !current.isUno()) {
            current.addCard(Game.getInstance().getDiscardPile().getTopCard());
            Game.getInstance().getDiscardPile().removeTopCard();
            Game.getInstance().getCurrentPlayer().setValidationUno(false);
            throw new PunishmentException("The current player don't say uno");
        }
    }

    /**
     * Checks if the current player is required to play a card.
     * <br><br>
     * This method verifies if the current player must play a card based on the game rules.
     * If they are required to play and fail to do so, an exception is thrown to indicate
     * that they must take their turn.
     *
     * @throws UnoException If the current player must play a card
     */
    private void checkPlayerMustPlay() throws UnoException {
        if(Game.getInstance().isCurrentPlayerMustPlay()) {
            throw new UnoException("You must play !");
        }
    }

    /**
     * Checks if the top card on the discard pile is a Wild card and requires a color choice.
     * <br><br>
     * This method ensures that if the top card is a Wild card, the player must choose a color
     * before proceeding. If they fail to do so, an exception is thrown to indicate that a color
     * must be selected.
     *
     * @throws UnoException If the top card is a Wild card and no color has been chosen
     */
    private void checkWildColorSet() throws UnoException {
        if(Game.getInstance().getCurrentColor() == Color.WILD) {
            throw new UnoException("You must chose a color!");
        }
    }

    //*******************************************************//
    //                      Other                            //
    //*******************************************************//


    /**
     * This method prints the player's hand to the console.
     * It iterates through the cards in the player's hand and prints each card.
     */
    public void printHand() {
        System.out.println("Hand of " + name + ":");
        for (Card card : hand) {
            System.out.println(card);
        }
    }

    /**
     * This method prints the player's name and their hand to the console.
     * It calls the printHand() method to display the cards in the player's hand.
     */
    public void printPlayer() {
        System.out.println("Name: " + name);
        printHand();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", hand=" + hand +
                ", unoValidation=" + unoValidation +
                '}';
    }
}
