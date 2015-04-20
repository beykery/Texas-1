/*
 * @(#)Pot.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import utils.PageUtils;

/**
 * @author Robin
 */
public class Pot {
    private double chips = 0.0f;
    private Set<Integer> participants = new HashSet<Integer>();

    public double getPerChips() {
        return chips;
    }

    public void setChips(double chips) {
        this.chips = chips;
    }

    public Set<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Integer> participants) {
        this.participants = participants;
    }

    public void addParticipant(int seat) {
        this.participants.add(seat);
    }

    public double getPotChips() {
        return chips * participants.size();
    }

    /**
     * 获取彩池
     */
    public static Queue<Pot> createPot(Map<Integer, Double> chipMap) {
        Queue<Pot> pots = new LinkedList<Pot>();
        List<Entry<Integer, Double>> entrys = PageUtils.sortEntry(chipMap);

        double lastLev = 0;
        while (!entrys.isEmpty()) {
            double currentLev = entrys.get(0).getValue();
            double pay = currentLev - lastLev;
            Pot pot = new Pot();
            pot.setChips(pay);
            int i = 0;
            for (i = 0; i < entrys.size(); i++) {
                double val = entrys.get(i).getValue();
                if (val - currentLev >= SystemConstants.EPS) {
                    lastLev = currentLev;
                    break;
                }
                int seat = entrys.get(i).getKey();
                pot.addParticipant(seat);
                entrys.remove(i--);
            }

            for (; i < entrys.size(); i++) {
                int seat = entrys.get(i).getKey();
                pot.addParticipant(seat);
            }

            pots.add(pot);
        }

        return pots;
    }
}
