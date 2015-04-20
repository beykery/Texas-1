/*
 * @(#)SystemConstants.java    Created on 2014年4月30日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package entity;

/**
 * @author Robin
 */
public abstract class SystemConstants {

    public static final double EPS = 0.01f;

    public static final int cardHeapCount = 1;

    public static final int cardCount = 52 * cardHeapCount;

    public static final int POCKET_COUNT = 2;

    public static final String FLOP_IS = "Flop is %s %s %s";

    public static final String TURN_IS = "Turn is %s";

    public static final String RIVER_IS = "River is %s";

    public static final double DEFAULT_CHIP = 200.0f;
}
