package com.hctrom.romcontrol.logcat.util;

public interface Function<E,T> {

	T apply(E input);
}
