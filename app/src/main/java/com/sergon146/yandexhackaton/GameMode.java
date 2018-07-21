package com.sergon146.yandexhackaton;

public class GameMode {

	public static final String LEVEL_INVERT = "LEVEL_INVERT";
	public static final String LEVEL_RANDOM = "LEVEL_RANDOM";
	public static final String LEVEL_HARDCORE = "LEVEL_HARDCORE";

	public static int getCoefficient(String param, Integer value) {
		switch(param) {
			case LEVEL_INVERT:
				return -2;
			case LEVEL_RANDOM:
				return Double.valueOf(Math.random() * (12f - (-12f)) + (-12f)).intValue();
			case LEVEL_HARDCORE:
				return value + 1;
			default:
				return 2;
		}
	}
}
