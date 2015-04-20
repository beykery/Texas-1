/*
 * @(#)Card.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

import java.util.List;

/**
 * @author Robin
 */
public class Card implements Comparable<Card> {
    public static final String SHAPS = "DCHS";
    public static final String NUMBERS = "23456789TJQKA";
    public static final String STRAIGHT_NUMBERS = "A23456789TJQK";

    private char shape;
    private char number;

    public Card(char shape, char number) {
        this.shape = shape;
        this.number = number;
    }

    public char getShape() {
        return shape;
    }

    public char getNumber() {
        return number;
    }

    public String getShapeText() {
        if (shape == 'S') {
            return "♠";
        }

        if (shape == 'H') {
            return "♥";
        }

        if (shape == 'C') {
            return "♣";
        }

        return "♦";
    }

    public String getCardText() {
        if (number == 'T') {
            return getShapeText() + "10";
        }
        return getShapeText() + number;
    }

    @Override
    public int compareTo(Card card) {
        return NUMBERS.indexOf(card.getNumber() + "") - NUMBERS.indexOf(number + "");
    }

    public static String getCardText(List<Card> cards) {
        if (cards == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (Card card : cards) {
            sb.append(card.getCardText());
            sb.append(" ");
        }

        return sb.toString();
    }
}
