package com.richyan.android.poker24points;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ruhua on 9/11/2016.
 */
public class Deck {
    private final List<Card> cards;

    public Deck(){
        String[] faces = {"ace", "two","three","four","five","six","seven","eight","nine","ten","jack","queen","king"};
        String[] suits = {"clubs","diamonds","hearts","spades"};
        cards = new ArrayList<>(52);
        for(String face: faces){
            for(String suit: suits){
                cards.add(new Card(face, suit));
            }
        }
    }
    public List<Card> getCards(){
        return cards;
    }

}
