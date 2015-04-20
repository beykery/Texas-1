/*
 * @(#)Player.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin
 */
public class Player {

    private String name;
    private int seat;
    private double chip = 0.0f;
    private List<Card> pockect = new ArrayList<Card>();

    public Player(int seat) {
        this.seat = seat;
    }

    public Player(String name, double chip) {
        this.name = name;
        this.chip = chip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getChip() {
        return chip;
    }

    public void setChip(double chip) {
        this.chip = chip;
    }

    public void changeChip(double chip) {
        this.chip += chip;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public List<Card> getPockect() {
        return pockect;
    }

    public void setPockect(List<Card> pockect) {
        this.pockect = pockect;
    }

    public void addPocketCard(Card card) {
        pockect.add(card);
    }

    public String getPockectText() {
        return Card.getCardText(pockect);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + seat;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        if (seat != other.seat) {
            return false;
        }
        return true;
    }

}
