/*
 * @(#)PageUtils.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import entity.SystemConstants;

/**
 * @author Robin
 */
public class PageUtils {

    /**
     * 输出
     */
    public static void output(String text, Object... args) {
        if (args == null || args.length == 0) {
            System.out.println(text);
        } else {
            System.out.println(String.format(text, args));
        }
    }

    public static void outputInline(String text, Object... args) {
        if (args == null || args.length == 0) {
            System.out.print(text);
        } else {
            System.out.print(String.format(text, args));
        }
    }

    public static List<Entry<Integer, Double>> sortEntry(Map<Integer, Double> map) {
        List<Entry<Integer, Double>> entrys = new ArrayList<Entry<Integer, Double>>();
        entrys.addAll(map.entrySet());
        Collections.sort(entrys, new Comparator<Entry<Integer, Double>>() {

            @Override
            public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
                double d = o1.getValue() - o2.getValue();
                if (d > SystemConstants.EPS) {
                    return 1;
                }
                if (d < -SystemConstants.EPS) {
                    return -1;
                }
                return 0;
            }

        });

        return entrys;
    }

    public static Map<Integer, Double> sortMap(Map<Integer, Double> map) {
        List<Entry<Integer, Double>> entrys = sortEntry(map);
        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Entry<Integer, Double> entry : entrys) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static boolean isValidCommand(String[] commands, double minBet, double minRaise) {
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

        if ("bt".equals(cmdType) && (value - minBet) < -SystemConstants.EPS) {
            System.out.print("Bet value less than minimum bet!");
            return false;
        }

        if ("rs".equals(cmdType) && (value - minRaise) < -SystemConstants.EPS) {
            System.out.print("Raise value less than minimum raise!");
            return false;
        }

        return true;
    }
}
