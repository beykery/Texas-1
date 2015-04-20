/*
 * @(#)Play.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import utils.PageUtils;
import entity.GameSetting;
import entity.Player;
import entity.Round;
import entity.SystemConstants;

/**
 * @author Robin
 */
public class Play {
    private static final String GAME_OVER = "Game over!";
    private static final String NOW_BUTTON = "Now Player %s is the button";
    private static final String HAS_ALL_IN = "Player %s has been all-in";
    private static final String ALL_IN_TO_CALL = "Player %s need all-in to call";
    private static final String BIG_BLIND = "Player %s big blind: %.2f";
    private static final String CALL = "Player %s need call %.2f";
    private static final String SHOWDOWN = "Showdown:";
    private static final String PLAYER_SHOWDOWN = "Player %s's cards: %s (%s)";
    private static final String SHOW_PUBLIC_CARD = "Public cards are: %s";
    private static final String INVALID_COMMAND = "invalid command";
    private static boolean FROM_FILE = false;
    private static Scanner in = null;
    private static final Map<String, String> commandMap = new HashMap<String, String>();
    private static List<Player> players = new ArrayList<Player>();
    private static int currentPlayerCount = 0;
    private static GameSetting setting = null;
    private static Round round = null;
    private static int button = 0;
    private static final int PLAYER_COUNT = 9;

    static {
        if (FROM_FILE) {
            try {
                in = new Scanner(new File("I://workspace-java/Test/src/cin"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            in = new Scanner(System.in);
        }
        commandMap.put("ck", "check");
        commandMap.put("bt", "bet");
        commandMap.put("cl", "call");
        commandMap.put("rs", "raise");
        commandMap.put("ai", "all-in");
        commandMap.put("fd", "fold");
    }

    public static void main(String[] args) {
        initGame();
        do {
            playRound();
        } while (changeButton());
        PageUtils.output(GAME_OVER);
    }

    private static void initGame() {
        initGameSetting();
        initPlayer();
    }

    /**
     * 初始化大小盲注和底注
     */
    private static void initGameSetting() {
        setting = new GameSetting(4, 1);
    }

    /**
     * 初始化玩家
     */
    private static void initPlayer() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            String name = (char) ('A' + i) + "";
            Player player = new Player(name, SystemConstants.DEFAULT_CHIP);
            player.setSeat(i);
            players.add(player);
        }
        currentPlayerCount = PLAYER_COUNT;
        button = new Random().nextInt(PLAYER_COUNT);
        PageUtils.output(NOW_BUTTON, players.get(button).getName());
    }

    /**
     * 切换庄家
     */
    private static boolean changeButton() {
        boolean change = false;
        List<Player> newPlayers = new ArrayList<Player>();
        for (int i = 0; i < currentPlayerCount; i++) {
            Player p = players.get(i);
            if (p.getChip() < SystemConstants.EPS) {
                System.out.println(String.format("player %s lose the game!", p.getName()));
                continue;
            }
            newPlayers.add(p);
            if (!change && i > button) {
                button = i;
                change = true;
            }
        }
        if (!change) {
            button = 0;
        }
        players = newPlayers;
        currentPlayerCount = newPlayers.size();
        PageUtils.output(NOW_BUTTON, players.get(button).getName());

        return currentPlayerCount > 1;
    }

    private static void playRound() {
        initRound();
        if (preFlop()) {
            payoff();
            return;
        }
        if (flop()) {
            payoff();
            return;
        }
        if (turn()) {
            payoff();
            return;
        }
        if (!river()) {
            showDown();
        }
        payoff();
    }

    private static void payoff() {

    }

    private static void showDown() {

    }

    /**
     * 河牌圈
     */
    private static boolean river() {
        round.burnCard();
        round.dealRiver();
        return doRoundBet(false);
    }

    /**
     * 转牌圈
     */
    private static boolean turn() {
        round.burnCard();
        round.dealTurn();
        return doRoundBet(false);
    }

    /**
     * 翻牌圈
     */
    private static boolean flop() {
        round.burnCard();
        round.dealFlop();
        return doRoundBet(false);
    }

    /**
     * 翻牌前
     * 
     * @return 是否结算
     */
    private static boolean preFlop() {
        return doRoundBet(true);
    }

    /**
     * 初始化每局
     */
    private static void initRound() {
        round = new Round();
        round.initRoundPlayer(button, players);
        round.initCardHeap();
        round.initAnte(setting.getAnte());
        round.initPocketCard();
    }

    /**
     * 一轮叫牌
     */
    public static boolean doRoundBet(boolean preFlop) {
        List<Player> roundPlayers = round.getRoundPlayers();
        int currentRoundPlayerCount = roundPlayers.size();
        Map<Integer, Double> chipMap = new HashMap<Integer, Double>();

        double minBet = setting.getBigBlind();
        double currentRaise = 0;
        double currentChip = 0;
        int end = 0;

        if (preFlop) {
            // small blind
            double smallBlind = setting.getSmallBlind();
            Player sb = roundPlayers.get(0);
            int sbSeat = sb.getSeat();
            if (sb.getChip() < smallBlind) {
                smallBlind = sb.getChip();
            }
            if (smallBlind >= SystemConstants.EPS) {
                chipMap.put(sbSeat, smallBlind);
            }
            // big blind
            double bigBlind = setting.getBigBlind();
            Player bb = roundPlayers.get(1);
            int bbSeat = bb.getSeat();
            if (bb.getChip() < bigBlind) {
                bigBlind = bb.getChip();
            }
            if (bigBlind >= SystemConstants.EPS) {
                chipMap.put(bbSeat, bigBlind);
            }

            currentRaise = setting.getBigBlind() - setting.getSmallBlind();
            currentChip = minBet;
            end = 2 % currentRoundPlayerCount;
        }

        int current = end;
        do {
            Player p = roundPlayers.get(current);
            String playerName = p.getName();
            Double userPotChips = chipMap.get(p.getSeat());
            if (userPotChips == null) {
                userPotChips = 0.0;
            }
            double hasChips = p.getChip() - userPotChips;
            if (hasChips < SystemConstants.EPS) {
                PageUtils.output(HAS_ALL_IN, playerName);
                current = (current + 1) % currentRoundPlayerCount;
                continue;
            }

            boolean isRaise = false;
            double minRaise = currentChip + currentRaise;
            Set<String> validCommand = new HashSet<String>();
            validCommand.add("fd");
            if (currentChip < SystemConstants.EPS) {
                validCommand.add("ck");
                validCommand.add("bt");
                validCommand.add("ai");
            } else {
                boolean shouldPay = (currentChip - userPotChips) >= SystemConstants.EPS;
                if (!shouldPay) {
                    validCommand.add("ck");
                }
                double handChip = p.getChip();
                double left = handChip - currentChip;
                if (left < SystemConstants.EPS) {
                    PageUtils.output(ALL_IN_TO_CALL, playerName);
                } else {
                    if (shouldPay) {
                        validCommand.add("cl");
                    }

                    if (handChip > minRaise) {
                        validCommand.add("rs");
                    }
                    double needToCall = currentChip - userPotChips;
                    if (preFlop && !shouldPay) {
                        PageUtils.output(BIG_BLIND, playerName, currentChip);
                    } else {
                        PageUtils.output(CALL, playerName, needToCall);
                        isRaise = true;
                    }
                }
                validCommand.add("ai");
            }
            PageUtils.outputInline("%s>:", playerName);
            String[] commands = in.nextLine().trim().split(" ");
            String cmdType = commands[0].trim();
            while (!validCommand.contains(cmdType) || !PageUtils.isValidCommand(commands, minBet, minRaise)) {
                PageUtils.output(INVALID_COMMAND);
                PageUtils.outputInline("%s>:", playerName);
                commands = in.nextLine().trim().split(" ");
                cmdType = commands[0].trim();
            }
            if ("ck".equals(cmdType)) {
                // do nothing
            }
            if ("fd".equals(cmdType)) {
                roundPlayers.remove(new Player(p.getSeat()));
                currentRoundPlayerCount--;
                if (currentRoundPlayerCount < 2) {
                    break;
                }
            }
            if ("bt".equals(cmdType)) {
                currentChip = Double.parseDouble(commands[1].trim());
                currentRaise = currentChip;
                chipMap.put(p.getSeat(), currentChip);
                end = current;
            } else if ("cl".equals(cmdType)) {
                chipMap.put(p.getSeat(), currentChip);
            } else if ("rs".equals(cmdType)) {
                double raiseTo = Double.parseDouble(commands[1].trim());
                currentRaise = raiseTo - currentChip;
                currentChip = raiseTo;
                chipMap.put(p.getSeat(), currentChip);
                end = current;
            } else if ("ai".equals(cmdType)) {
                double raiseTo = p.getChip();
                if (isRaise) {
                    currentRaise = raiseTo - currentChip;
                    currentChip = raiseTo;
                    chipMap.put(p.getSeat(), currentChip);
                    end = current;
                } else {
                    chipMap.put(p.getSeat(), raiseTo);
                }
            }
            current = (current + 1) % currentRoundPlayerCount;
        } while (current != end);

        round.addBetMap(chipMap);

        return roundPlayers.size() < 2;
    }
}
