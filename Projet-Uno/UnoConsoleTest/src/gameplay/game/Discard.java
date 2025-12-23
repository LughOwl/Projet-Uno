package gameplay.game;

import gameplay.card.Card;

import java.util.ArrayList;

public class Discard {
    private final ArrayList<Card> discardPile = new ArrayList<>();

    public Discard(){}

    public void addCard(Card card){
        if(card == null){
            throw new IllegalArgumentException("Card cannot be null");
        }
        discardPile.add(card);
    }

    public Card getTopCard(){
        if(discardPile.isEmpty()){
            return null;
        }
        return discardPile.getLast();
    }

    public void clear(){
        discardPile.clear();
    }

    public ArrayList<Card> getDiscardPile(){
        return discardPile;
    }
}
