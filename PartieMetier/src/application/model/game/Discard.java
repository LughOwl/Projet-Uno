package application.model.game;

import application.model.card.Card;
import application.model.card.special.Wild;
import application.model.card.special.draw.WildDrawFour;
import application.model.exception.UnoException;

import java.util.ArrayList;

public class Discard {
    private final ArrayList<Card> discardPile = new ArrayList<>();

    public Discard(){}


    //*********************//
    // Getters and setters //
    //*********************//

    public void addCard(Card card){
        if(card == null){
            throw new IllegalArgumentException("Card cannot be null");
        }
        discardPile.add(card);
        //TODO ?? Pk on modifie la couleur du jeu ?
        if (!(card instanceof Wild) && !(card instanceof WildDrawFour))
            Game.getInstance().setCurrentColor(card.getColor());
    }

    public Card getTopCard(){
        if(discardPile.isEmpty()){
            return null;
        }
        return discardPile.getLast();
    }

    public void removeTopCard() throws UnoException {
        if(discardPile.isEmpty()){
            throw new UnoException("DiscardPile is empty");
        }
        discardPile.removeLast();
    }


    public void clear(){
        discardPile.clear();
    }

    public ArrayList<Card> getDiscardPile(){
        return discardPile;
    }

    public boolean isEmpty() {
        return discardPile.isEmpty();
    }

    public int size() {
        return discardPile.size();
    }

    //****************************//
    // End of getters and setters //
    //****************************//


    @Override
    public String toString() {
        return "Discard{" +
                "discardPile=" + discardPile +
                '}';
    }
}
