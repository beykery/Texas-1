package client;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Robin
 */
public class Main {
    private static boolean FROM_FILE = true;
    private static final String GAME_OVER = "Game over!";
    private static final String SHAPS = "DCHS";
    private static final String NUMBERS = "23456789TJQKA";
    private static final String STRAIGHT_NUMBERS = "A23456789TJQK";
    private static final int cardHeapCount = 1;
    private static final int cardCount = 52 * cardHeapCount;
    private static final int PLAYER_COUNT = 9;
    private static final double DEFAULT_CHIP = 200.0f;
    private static final String[] POSITIONS = { "SB", "BB", "UTG", "UTG+1", "HJ", "CO", "D" };
    private static final Map<String, String> commandMap = new HashMap<String, String>();
    private static final int POCKET_COUNT = 2;
    private static final double EPS = 0.01f;
    private static final String NOW_BUTTON = "Now Player %s is the button";
    private static final String HAS_ALL_IN = "Player %s has been all-in";
    private static final String ALL_IN_TO_CALL = "Player %s need all-in to call";
    private static final String FLOP_IS = "Flop is %s %s %s";
    private static final String TURN_IS = "Turn is %s";
    private static final String RIVER_IS = "River is %s";
    private static final String CALL = "Player %s need call %.2f";
    private static final String BIG_BLIND = "Player %s big blind: %.2f";
    private static final String SHOWDOWN = "Showdown:";
    private static final String PLAYER_SHOWDOWN = "Player %s's cards: %s (%s)";
    private static final String SHOW_PUBLIC_CARD = "Public cards are: %s";
    private static final String INVALID_COMMAND = "invalid command";
    private static Scanner in = null;
    private static List<Player> players = new ArrayList<Player>();
    private static int currentPlayerCount = 0;
    private static GameSetting setting = null;
    private static Round round = null;
    private static int button = 0;

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
        output(GAME_OVER);
    }

    private static class GameSetting {
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
            return ante >= EPS;
        }
    }

    private static class Card implements Comparable<Card> {
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
    }

    private static class ShowdownCard implements Comparable<ShowdownCard> {
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
                        if (num == NUMBERS.indexOf(c.getNumber() + "")) {
                            cards.add(c);
                            break;
                        }
                    }
                }
                break;
            case 1:
                for (int num : maxCards) {
                    for (Card c : cs) {
                        if (num == NUMBERS.indexOf(c.getNumber() + "")) {
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
                    if (pair1 == NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
                for (Card c : cs) {
                    if (pair2 == NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
                for (Card c : cs) {
                    if (cd == NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                        break;
                    }
                }
                break;
            case 3:
                for (int num : maxCards) {
                    for (Card c : cs) {
                        if (num == NUMBERS.indexOf(c.getNumber() + "")) {
                            cards.add(c);
                        }
                    }
                }
                break;
            case 4:
                int start = maxCards.get(0);
                for (int i = start; i < start + 5; i++) {
                    for (Card c : cs) {
                        if ((i % 13) == STRAIGHT_NUMBERS.indexOf(c.getNumber() + "")) {
                            cards.add(c);
                            break;
                        }
                    }
                }
                break;
            case 5:
                for (int num : maxCards) {
                    for (Card c : cs) {
                        if (shape == c.getShape() && num == NUMBERS.indexOf(c.getNumber() + "")) {
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
                    if (set == NUMBERS.indexOf(c.getNumber() + "")) {
                        cards.add(c);
                    }
                }
                int cnt = 0;
                for (Card c : cs) {
                    if (pair == NUMBERS.indexOf(c.getNumber() + "")) {
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
                        if (num == NUMBERS.indexOf(c.getNumber() + "")) {
                            cards.add(c);
                        }
                    }
                }
                break;
            case 8:
                int st = maxCards.get(0);
                for (int i = st; i < st + 5; i++) {
                    for (Card c : cs) {
                        if (shape == c.getShape() && (i % 13) == STRAIGHT_NUMBERS.indexOf(c.getNumber() + "")) {
                            cards.add(c);
                            break;
                        }
                    }
                }
            }

            setCardsText(getCardText(cards));
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

    private static class Player {
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
            return getCardText(pockect);
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

    private static class Pot {
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
    }

    private static class Round {
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
                Player p = round.getRoundPlayer(seat);
                p.changeChip(-betValue);
            }

            pots = createPot(chipMap);
        }

        /**
         * 初始化牌堆
         */
        public void initCardHeap() {
            int[][] visited = new int[4][13];
            Random random = new Random();
            for (int i = 0; i < cardCount; i++) {
                int shp = random.nextInt(4);
                int num = random.nextInt(13);
                while (visited[shp][num] >= cardHeapCount) {
                    shp = random.nextInt(4);
                    num = random.nextInt(13);
                }
                cardHeap.add(new Card(SHAPS.charAt(shp), NUMBERS.charAt(num)));
                visited[shp][num]++;
            }
        }

        /**
         * 初始化每局的玩家
         */
        public void initRoundPlayer() {
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
        public void initAnte() {
            if (setting.needAnte()) {
                Map<Integer, Double> anteMap = new HashMap<Integer, Double>();
                double ante = setting.getAnte();
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
            } while (round < POCKET_COUNT || player != 0);
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
            output(FLOP_IS, flop.get(0).getCardText(), flop.get(1).getCardText(), flop.get(2).getCardText());
        }

        /**
         * 转牌圈发牌
         */
        public void dealTurn() {
            turn = cardHeap.poll();
            output(TURN_IS, turn.getCardText());
        }

        /**
         * 河牌圈发牌
         */
        public void dealRiver() {
            river = cardHeap.poll();
            output(RIVER_IS, river.getCardText());
        }
    }

    private static void initGame() {
        initGameSetting();
        initPlayer();
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

    /**
     * 输出
     */
    private static void output(String text, Object... args) {
        if (args == null || args.length == 0) {
            System.out.println(text);
        } else {
            System.out.println(String.format(text, args));
        }
    }

    private static void outputInline(String text, Object... args) {
        if (args == null || args.length == 0) {
            System.out.print(text);
        } else {
            System.out.print(String.format(text, args));
        }
    }

    /**
     * 结算
     */
    private static void payoff() {
        Map<Integer, Integer> playRank = round.getShowdownRank();
        Queue<Pot> pots = round.getPots();
        while (!pots.isEmpty()) {
            Pot pot = pots.poll();
            double chips = pot.getPotChips();
            Set<Integer> in = pot.getParticipants();
            Integer[] participants = in.toArray(new Integer[in.size()]);
            int participantSize = participants.length;
            if (participantSize == 0) {
                // TODO No body  ?
            } else if (participantSize == 1) {
                Player p = round.getRoundPlayer(participants[0]);
                p.changeChip(chips);
            } else {
                int maxRank = currentPlayerCount + 1;
                Set<Integer> wins = new HashSet<Integer>();
                for (Integer seat : participants) {
                    Integer rank = playRank.get(seat);
                    if (rank == null) { // fold
                        continue;
                    }
                    if (rank < maxRank) {
                        maxRank = rank;
                        wins.clear();
                        wins.add(seat);
                    } else if (rank == maxRank) {
                        wins.add(seat);
                    }
                }

                chips = chips / wins.size();
                for (Integer seat : wins) {
                    Player p = round.getRoundPlayer(seat);
                    p.changeChip(chips);
                }
            }
        }
    }

    /**
     * 比大小
     */
    private static void showDown() {
        List<Player> left = round.getRoundPlayers();
        List<Card> publicCard = new ArrayList<Card>();
        publicCard.addAll(round.getFlop());
        publicCard.add(round.getTurn());
        publicCard.add(round.getRiver());
        output(SHOW_PUBLIC_CARD, getCardText(publicCard));
        Map<Integer, ShowdownCard> showdownMap = new LinkedHashMap<Integer, ShowdownCard>();
        for (Player p : left) {
            List<Card> cards = new ArrayList<Card>();
            cards.addAll(publicCard);
            cards.addAll(p.getPockect());
            ShowdownCard card = getMaxCard(cards);
            showdownMap.put(p.getSeat(), card);
        }
        outputShowdown(showdownMap);
        round.setShowdownRank(sortShowdown(showdownMap));
    }

    /**
     * 从N张牌中获取组成的最大的5张
     * 
     * @return 返回牌型0, 1, 2, 3, 4, 5 , 6, 7, 8
     */
    private static ShowdownCard getMaxCard(List<Card> cards) {
        ShowdownCard result = new ShowdownCard();
        int[] numbers = new int[13];
        int[] shaps = new int[4];
        int flush = -1;
        int bomb = -1;
        int maxSet = -1;
        int maxPair = -1;
        int secondPair = -1;
        for (Card card : cards) {
            int shp = SHAPS.indexOf(card.getShape() + "");
            int num = NUMBERS.indexOf(card.getNumber() + "");
            shaps[shp]++;
            if (shaps[shp] > 4) {
                flush = shp;
            }
            numbers[num]++;
            if (numbers[num] > 3) {
                bomb = num;
            } else if (numbers[num] > 2) {
                if (num > maxSet) {
                    maxSet = num;
                    if (maxSet == maxPair) {
                        maxPair = secondPair;
                        secondPair = -1;
                    } else if (maxSet == secondPair) {
                        secondPair = -1;
                    }
                } else if (num > maxPair) {
                    maxPair = num;
                }
            } else if (numbers[num] > 1) {
                if (num > maxPair) {
                    secondPair = maxPair;
                    maxPair = num;
                } else if (num > secondPair) {
                    secondPair = num;
                }
            }
        }
        List<Integer> maxs = new ArrayList<Integer>();
        if (flush >= 0) {
            List<Card> flushCard = new ArrayList<Card>();
            for (Card card : cards) {
                if (flush == SHAPS.indexOf(card.getShape() + "")) {
                    flushCard.add(card);
                }
            }
            int straight = checkStraight(flushCard);
            if (straight >= 0) {
                result.setCardType(8);
                maxs.add(straight);
                result.setMaxCards(maxs);
            } else {
                result.setCardType(5);
                maxs = getHighCards(flushCard);
            }
            result.setShape(SHAPS.charAt(flush));
        } else if (bomb >= 0) {
            result.setCardType(7);
            maxs.add(bomb);
            maxs.addAll(getHighCards(1, getOtherCards(bomb, cards)));

        } else {
            if (maxSet >= 0 && maxPair >= 0) {
                result.setCardType(6);
                maxs.add(maxSet);
                maxs.add(maxPair);
            } else {
                int straight = checkStraight(cards);
                if (straight >= 0) {
                    result.setCardType(4);
                    maxs.add(straight);
                } else if (maxSet >= 0) {
                    result.setCardType(3);
                    maxs.add(maxSet);
                    List<Integer> cs = getHighCards(2, getOtherCards(maxSet, cards));
                    for (Integer i : cs) {
                        maxs.add(i);
                    }
                } else if (secondPair >= 0) {
                    result.setCardType(2);
                    maxs.add(maxPair);
                    maxs.add(secondPair);
                    List<Integer> cs = getHighCards(1, getOtherCards(maxPair, secondPair, cards));
                    maxs.addAll(cs);
                } else if (maxPair >= 0) {
                    result.setCardType(1);
                    maxs.add(maxPair);
                    List<Integer> cs = getHighCards(3, getOtherCards(maxPair, cards));
                    for (Integer i : cs) {
                        maxs.add(i);
                    }
                } else {
                    result.setCardType(0);
                    maxs = getHighCards(cards);
                }
            }
        }
        result.setMaxCards(maxs);
        result.setMaxCardWithAllCards(cards);

        return result;
    }

    private static List<Card> getOtherCards(int num1, int num2, List<Card> cards) {
        List<Card> otherCard = new ArrayList<Card>();
        for (Card card : cards) {
            int idx = NUMBERS.indexOf(card.getNumber() + "");
            if (num1 != idx && num2 != idx) {
                otherCard.add(card);
            }
        }

        return otherCard;
    }

    private static String getCardText(List<Card> cards) {
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

    private static List<Card> getOtherCards(int num, List<Card> cards) {
        List<Card> otherCard = new ArrayList<Card>();
        for (Card card : cards) {
            if (num != NUMBERS.indexOf(card.getNumber() + "")) {
                otherCard.add(card);
            }
        }

        return otherCard;
    }

    /**
     * 获取牌堆中 @param cardCount 张高牌
     * 
     * @return
     */
    private static List<Integer> getHighCards(int cardCount, List<Card> cards) {
        if (cards == null) {
            return null;
        }
        Collections.sort(cards);
        List<Integer> newList = new ArrayList<Integer>();
        if (cards.size() < cardCount) {
            cardCount = cards.size();
        }
        for (int i = 0; i < cardCount; i++) {
            newList.add(NUMBERS.indexOf(cards.get(i).getNumber() + ""));
        }

        return newList;
    }

    /**
     * 获取牌堆中的5张高牌
     * 
     * @return
     */
    private static List<Integer> getHighCards(List<Card> cards) {
        return getHighCards(5, cards);
    }

    /**
     * 按showdown大小排序
     */
    private static Map<Integer, Integer> sortShowdown(Map<Integer, ShowdownCard> showdownMap) {
        if (showdownMap == null) {
            return null;
        }

        List<Entry<Integer, ShowdownCard>> sorted = new LinkedList<Entry<Integer, ShowdownCard>>();
        for (Entry<Integer, ShowdownCard> entry : showdownMap.entrySet()) {
            boolean inserted = false;
            for (int i = 0; i < sorted.size(); i++) {
                ShowdownCard inserter = entry.getValue();
                ShowdownCard s = sorted.get(i).getValue();
                if (inserter.compareTo(s) >= 0) {
                    sorted.add(i, entry);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sorted.add(entry);
            }
        }

        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        Entry<Integer, ShowdownCard> prev = sorted.get(0);
        result.put(prev.getKey(), 1);
        for (int i = 1; i < sorted.size(); i++) {
            Entry<Integer, ShowdownCard> current = sorted.get(i);
            if (current.getValue().compareTo(prev.getValue()) == 0) {
                int prevRank = result.get(prev.getKey());
                result.put(current.getKey(), prevRank);
            } else {
                result.put(current.getKey(), result.size() + 1);
            }
            prev = current;
        }

        return result;
    }

    private static void outputShowdown(Map<Integer, ShowdownCard> showdownMap) {
        if (showdownMap == null) {
            return;
        }

        output(SHOWDOWN);
        for (Entry<Integer, ShowdownCard> entry : showdownMap.entrySet()) {
            Player p = round.getRoundPlayer(entry.getKey());
            ShowdownCard sc = entry.getValue();
            output(PLAYER_SHOWDOWN, p.getName(), sc.getCardsText(), p.getPockectText());
        }
    }

    /**
     * 是否是顺子
     * 
     * @return 最大的顺子
     */
    private static int checkStraight(List<Card> cards) {
        if (cards == null || cards.size() < 5) {
            return -1;
        }

        int[] nums = new int[13];
        for (Card card : cards) {
            nums[STRAIGHT_NUMBERS.indexOf(card.getNumber() + "")]++;
        }

        for (int i = 9; i >= 0; i--) {
            int cnt = 0;
            for (int j = 0; j < 5; j++) {
                if (nums[(i + j) % 13] > 0) {
                    cnt++;
                }
            }
            if (cnt == 5) {
                return i;
            }
        }

        return -1;
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
     * 一轮叫牌
     */
    private static boolean doRoundBet(boolean preFlop) {
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
            if (smallBlind >= EPS) {
                chipMap.put(sbSeat, smallBlind);
            }
            // big blind
            double bigBlind = setting.getBigBlind();
            Player bb = roundPlayers.get(1);
            int bbSeat = bb.getSeat();
            if (bb.getChip() < bigBlind) {
                bigBlind = bb.getChip();
            }
            if (bigBlind >= EPS) {
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
            if (hasChips < EPS) {
                output(HAS_ALL_IN, playerName);
                current = (current + 1) % currentRoundPlayerCount;
                continue;
            }

            boolean isRaise = false;
            double minRaise = currentChip + currentRaise;
            Set<String> validCommand = new HashSet<String>();
            validCommand.add("fd");
            if (currentChip < EPS) {
                validCommand.add("ck");
                validCommand.add("bt");
                validCommand.add("ai");
            } else {
                boolean shouldPay = (currentChip - userPotChips) >= EPS;
                if (!shouldPay) {
                    validCommand.add("ck");
                }
                double handChip = p.getChip();
                double left = handChip - currentChip;
                if (left < EPS) {
                    output(ALL_IN_TO_CALL, playerName);
                } else {
                    if (shouldPay) {
                        validCommand.add("cl");
                    }

                    if (handChip > minRaise) {
                        validCommand.add("rs");
                    }
                    double needToCall = currentChip - userPotChips;
                    if (preFlop && !shouldPay) {
                        output(BIG_BLIND, playerName, currentChip);
                    } else {
                        output(CALL, playerName, needToCall);
                        isRaise = true;
                    }
                }
                validCommand.add("ai");
            }
            outputInline("%s>:", playerName);
            String[] commands = in.nextLine().trim().split(" ");
            String cmdType = commands[0].trim();
            while (!validCommand.contains(cmdType) || !isValidCommand(commands, minBet, minRaise)) {
                output(INVALID_COMMAND);
                outputInline("%s>:", playerName);
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

    private static boolean isValidCommand(String[] commands, double minBet, double minRaise) {
        String cmdType = commands[0].trim();
        if (("bt".equals(cmdType) || "rs".equals(cmdType)) && commands.length < 2) {
            System.out.print("Need value parameter!");
            return false;
        }

        double value = 0.0f;
        try {
            value = Double.parseDouble(commands[1].trim());
        } catch (Exception e) {
        }

        if ("bt".equals(cmdType) && (value - minBet) < -EPS) {
            System.out.print("Bet value less than minimum bet!");
            return false;
        }

        if ("rs".equals(cmdType) && (value - minRaise) < -EPS) {
            System.out.print("Raise value less than minimum raise!");
            return false;
        }

        return true;
    }

    /**
     * 初始化大小盲注和底注
     */
    private static void initGameSetting() {
        setting = new GameSetting(4, 1);
    }

    /**
     * 初始化每局
     */
    private static void initRound() {
        round = new Round();
        round.initRoundPlayer();
        round.initCardHeap();
        round.initAnte();
        round.initPocketCard();
    }

    /**
     * 初始化玩家
     */
    private static void initPlayer() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            String name = (char) ('A' + i) + "";
            Player player = new Player(name, DEFAULT_CHIP);
            player.setSeat(i);
            players.add(player);
        }
        currentPlayerCount = PLAYER_COUNT;
        button = new Random().nextInt(PLAYER_COUNT);
        output(NOW_BUTTON, players.get(button).getName());
    }

    /**
     * 切换庄家
     */
    private static boolean changeButton() {
        boolean change = false;
        List<Player> newPlayers = new ArrayList<Player>();
        for (int i = 0; i < currentPlayerCount; i++) {
            Player p = players.get(i);
            if (p.getChip() < EPS) {
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
        output(NOW_BUTTON, players.get(button).getName());

        return currentPlayerCount > 1;
    }

    /**
     * 获取彩池
     */
    private static Queue<Pot> createPot(Map<Integer, Double> chipMap) {
        Queue<Pot> pots = new LinkedList<Pot>();
        List<Entry<Integer, Double>> entrys = sortEntry(chipMap);

        double lastLev = 0;
        while (!entrys.isEmpty()) {
            double currentLev = entrys.get(0).getValue();
            double pay = currentLev - lastLev;
            Pot pot = new Pot();
            pot.setChips(pay);
            int i = 0;
            for (i = 0; i < entrys.size(); i++) {
                double val = entrys.get(i).getValue();
                if (val - currentLev >= EPS) {
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

    private static List<Entry<Integer, Double>> sortEntry(Map<Integer, Double> map) {
        List<Entry<Integer, Double>> entrys = new ArrayList<Entry<Integer, Double>>();
        entrys.addAll(map.entrySet());
        Collections.sort(entrys, new Comparator<Entry<Integer, Double>>() {

            @Override
            public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
                double d = o1.getValue() - o2.getValue();
                if (d > EPS) {
                    return 1;
                }
                if (d < -EPS) {
                    return -1;
                }
                return 0;
            }

        });

        return entrys;
    }

    private static Map<Integer, Double> sortMap(Map<Integer, Double> map) {
        List<Entry<Integer, Double>> entrys = sortEntry(map);
        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Entry<Integer, Double> entry : entrys) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
