/*
 * @(#)Round.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import utils.PageUtils;

/**
 * @author Robin
 */
public class Round {

    private Queue<Card> cardHeap = new LinkedList<Card>();
    private Queue<Pot> pots = new LinkedList<Pot>();
    private List<Player> roundPlayers = new ArrayList<Player>();
    private Map<Integer, Double> chipMap = new HashMap<Integer, Double>();
    private List<Card> flop = null;
    private Card turn = null;
    private Card river = null;

    private Map<Integer, Integer> showdownRank = null;

    public Queue<Card> getCardHeap() {
        return cardHeap;
    }

    public void setCardHeap(Queue<Card> cardHeap) {
        this.cardHeap = cardHeap;
    }

    public Queue<Pot> getPots() {
        return pots;
    }

    public void setPots(Queue<Pot> pots) {
        this.pots = pots;
    }

    public List<Player> getRoundPlayers() {
        return roundPlayers;
    }

    public Player getRoundPlayer(int seat) {
        return getPlayerBySeat(seat);
    }

    /**
     * 根据座位号获取玩家
     */
    private Player getPlayerBySeat(int seat) {
        int size = roundPlayers.size();
        for (int i = 0; i < size; i++) {
            Player p = roundPlayers.get(i);
            if (p.getSeat() == seat) {
                return p;
            }
        }

        return null;
    }

    public void setRoundPlayers(List<Player> roundPlayers) {
        this.roundPlayers = roundPlayers;
    }

    public Map<Integer, Double> getChipMap() {
        return chipMap;
    }

    public void setChipMap(Map<Integer, Double> chipMap) {
        this.chipMap = chipMap;
    }

    public List<Card> getFlop() {
        return flop;
    }

    public void setFlop(List<Card> flop) {
        this.flop = flop;
    }

    public Card getTurn() {
        return turn;
    }

    public void setTurn(Card turn) {
        this.turn = turn;
    }

    public Card getRiver() {
        return river;
    }

    public void setRiver(Card river) {
        this.river = river;
    }

    public Map<Integer, Integer> getShowdownRank() {
        return showdownRank;
    }

    public void setShowdownRank(Map<Integer, Integer> showdownRank) {
        this.showdownRank = showdownRank;
    }

    public void addRoundPlayer(Player player) {
        roundPlayers.add(player);
    }

    /**
     * 添加投注资金
     */
    public void addBetMap(Map<Integer, Double> betMap) {
        if (betMap == null) {
            return;
        }

        for (Entry<Integer, Double> entry : betMap.entrySet()) {
            Integer seat = entry.getKey();
            Double betValue = entry.getValue();
            Double v = chipMap.get(seat);
            if (v == null) {
                v = 0.0;
            }
            v += betValue;
            chipMap.put(seat, v);
            Player p = getRoundPlayer(seat);
            p.changeChip(-betValue);
        }

        pots = Pot.createPot(chipMap);
    }

    /**
     * 初始化牌堆
     */
    public void initCardHeap() {
        int[][] visited = new int[4][13];
        Random random = new Random();
        for (int i = 0; i < SystemConstants.cardCount; i++) {
            int shp = random.nextInt(4);
            int num = random.nextInt(13);
            while (visited[shp][num] >= SystemConstants.cardHeapCount) {
                shp = random.nextInt(4);
                num = random.nextInt(13);
            }
            cardHeap.add(new Card(Card.SHAPS.charAt(shp), Card.NUMBERS.charAt(num)));
            visited[shp][num]++;
        }
    }

    /**
     * 初始化每局的玩家
     */
    public void initRoundPlayer(int button, List<Player> players) {
        if (players == null || players.size() == 0) {
            return;
        }

        int currentPlayerCount = players.size();
        int sbIdx = (button + 1) % currentPlayerCount;
        int current = sbIdx;
        do {
            addRoundPlayer(players.get(current));
            current = (current + 1) % currentPlayerCount;
        } while (current != sbIdx);
    }

    /**
     * 初始化Ante
     */
    public void initAnte(double ante) {
        Map<Integer, Double> anteMap = new HashMap<Integer, Double>();
        int roundPlayerCount = roundPlayers.size();
        for (int i = 0; i < roundPlayerCount; i++) {
            Player p = roundPlayers.get(i);
            if (p.getChip() < ante) {
                ante = p.getChip();
            }
            anteMap.put(p.getSeat(), ante);
        }
        addBetMap(anteMap);
    }

    /**
     * 初始化手牌
     */
    public void initPocketCard() {
        int currentRoundPlayerCount = roundPlayers.size();
        int round = 0;
        int player = 0;
        do {
            Player p = roundPlayers.get(player);
            p.addPocketCard(cardHeap.poll());
            player = (player + 1) % currentRoundPlayerCount;
            if (player == 0) {
                round++;
            }
        } while (round < SystemConstants.POCKET_COUNT || player != 0);
    }

    /**
     * 销牌
     */
    public boolean burnCard() {
        return burnCard(1);
    }

    public boolean burnCard(int count) {
        while (count > 0) {
            if (cardHeap.isEmpty()) {
                return false;
            }

            cardHeap.poll();
            count--;
        }

        return true;
    }

    /**
     * 翻牌圈发牌
     */
    public void dealFlop() {
        flop = new ArrayList<Card>();
        for (int i = 0; i < 3; i++) {
            flop.add(cardHeap.poll());
        }
        PageUtils.output(SystemConstants.FLOP_IS, flop.get(0).getCardText(), flop.get(1).getCardText(), flop.get(2)
                .getCardText());
    }

    /**
     * 转牌圈发牌
     */
    public void dealTurn() {
        turn = cardHeap.poll();
        PageUtils.output(SystemConstants.TURN_IS, turn.getCardText());
    }

    /**
     * 河牌圈发牌
     */
    public void dealRiver() {
        river = cardHeap.poll();
        PageUtils.output(SystemConstants.RIVER_IS, river.getCardText());
    }

}
