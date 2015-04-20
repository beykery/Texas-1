/*
 * @(#)ShowdownCard.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin
 */
public class ShowdownCard implements Comparable<ShowdownCard> {

    private int cardType;
    private List<Integer> maxCards;
    private List<Card> cards;
    private char shape;

    private String cardsText;

    public char getShape() {
        return shape;
    }

    public void setShape(char shape) {
        this.shape = shape;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public List<Integer> getMaxCards() {
        return maxCards;
    }

    public void setMaxCards(List<Integer> maxCards) {
        this.maxCards = maxCards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setMaxCardWithAllCards(List<Card> cs) {
        cards = new ArrayList<Card>();
        switch (cardType) {
        case 0:
            for (int num : maxCards) {
                for (Card c : cs) {
                    if (num == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                        break;
                    }
                }
            }
            break;
        case 1:
            for (int num : maxCards) {
                for (Card c : cs) {
                    if (num == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
            }
            break;
        case 2:
            int pair1 = maxCards.get(0);
            int pair2 = maxCards.get(1);
            int cd = maxCards.get(2);
            for (Card c : cs) {
                if (pair1 == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                    cards.add(c);
                }
            }
            for (Card c : cs) {
                if (pair2 == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                    cards.add(c);
                }
            }
            for (Card c : cs) {
                if (cd == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                    cards.add(c);
                    break;
                }
            }
            break;
        case 3:
            for (int num : maxCards) {
                for (Card c : cs) {
                    if (num == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
            }
            break;
        case 4:
            int start = maxCards.get(0);
            for (int i = start; i < start + 5; i++) {
                for (Card c : cs) {
                    if ((i % 13) == Card.STRAIGHT_NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                        break;
                    }
                }
            }
            break;
        case 5:
            for (int num : maxCards) {
                for (Card c : cs) {
                    if (shape == c.getShape() && num == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                        break;
                    }
                }
            }
            break;
        case 6:
            int set = maxCards.get(0);
            int pair = maxCards.get(1);
            for (Card c : cs) {
                if (set == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                    cards.add(c);
                }
            }
            int cnt = 0;
            for (Card c : cs) {
                if (pair == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                    cards.add(c);
                    if ((++cnt) == 2) {
                        break;
                    }
                }
            }
            break;
        case 7:
            for (int num : maxCards) {
                for (Card c : cs) {
                    if (num == Card.NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
            }
            break;
        case 8:
            int st = maxCards.get(0);
            for (int i = st; i < st + 5; i++) {
                for (Card c : cs) {
                    if (shape == c.getShape() && (i % 13) == Card.STRAIGHT_NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                        break;
                    }
                }
            }
        }

        setCardsText(Card.getCardText(cards));
    }

    @Override
    public int compareTo(ShowdownCard card) {
        int delta = cardType - card.getCardType();
        if (delta == 0) {
            List<Integer> mxc = card.getMaxCards();
            int size = maxCards.size();
            for (int i = 0; i < size; i++) {
                delta = maxCards.get(i) - mxc.get(i);
                if (delta == 0) {
                    continue;
                }
                return delta;
            }
            return 0;
        }

        return delta;
    }

    public String getCardsText() {
        return cardsText;
    }

    public void setCardsText(String cardsText) {
        this.cardsText = cardsText;
    }
}
