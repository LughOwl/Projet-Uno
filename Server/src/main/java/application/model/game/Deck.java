package application.model.game;

import application.model.card.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final ArrayList<Card> drawPile = new ArrayList<>();

    public Deck(){}

    //*********************//
    // Getters and setters //
    //*********************//

    public void addCard(Card card){
        if(card == null){
            throw new IllegalArgumentException("Card cannot be null");
        }
        drawPile.add(card);
    }

    public Card getTopCard(){
        if(drawPile.isEmpty()){
            return null;
        }
        return drawPile.getFirst();
    }

    public void clear(){
        drawPile.clear();
    }

    public ArrayList<Card> getDrawPile(){
        return drawPile;
    }

    public Card drawCard(){
        if(drawPile.isEmpty()){
            return null;
        }
        return drawPile.removeFirst();
    }

    public boolean isEmpty() {
        return drawPile.isEmpty();
    }

    public int size() {
        return drawPile.size();
    }

    public void addAll(ArrayList<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return;
        }
        drawPile.addAll(cards);
    }

    //****************************//
    // End of getters and setters //
    //****************************//

    /**
     * Shuffles the deck using Collections.shuffle()
     */
    public void shuffle() {
        Collections.shuffle(drawPile);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "drawPile=" + drawPile +
                '}';
    }
}
