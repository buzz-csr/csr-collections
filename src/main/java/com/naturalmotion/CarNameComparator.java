package com.naturalmotion;

import java.util.Comparator;
import java.util.Map.Entry;

import javax.json.JsonValue;

public class CarNameComparator implements Comparator<Entry<String, JsonValue>> {

	@Override
	public int compare(Entry<String, JsonValue> arg0, Entry<String, JsonValue> arg1) {
		return arg0.getKey().compareTo(arg1.getKey());
	}

}
