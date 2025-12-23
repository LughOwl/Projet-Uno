package gameplay.game;

import gameplay.card.Card;
import gameplay.utils.Utils;

import java.util.ArrayList;

public class Deck {
    private ArrayList<Card> discardPile = new ArrayList<>();

    public Deck(){}

    public Deck(int numberOfDecks){
        discardPile = Utils.generateData(numberOfDecks);
    }

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

    public Card drawCard(){
        if(discardPile.isEmpty()){
            return null;
        }
        return discardPile.removeLast();
    }

    public void random() {
        Utils.random(discardPile);
    }


}
