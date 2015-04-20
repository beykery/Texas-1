/*
 * @(#)GameSetting.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

/**
 * @author Robin
 */
public class GameSetting {

    private double bigBlind;
    private double smallBlind;
    private double ante = 0.0f;

    public GameSetting(double big) {
        this.bigBlind = big;
        this.smallBlind = big / 2;
    }

    public GameSetting(double big, double ante) {
        this.bigBlind = big;
        this.smallBlind = big / 2;
        this.ante = ante;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(double smallBlind) {
        this.smallBlind = smallBlind;
    }

    public double getAnte() {
        return ante;
    }

    public void setAnte(double ante) {
        this.ante = ante;
    }

    public boolean needAnte() {
        return ante >= SystemConstants.EPS;
    }
}
