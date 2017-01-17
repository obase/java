package com.test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.obase.Message;
import com.github.obase.json.Jsons;

public class TestMain {

	static final String json = "{\"errno\":0,\"data\":[[\"key0\",\"val0\"],[\"key1\",\"val1\"]]}";

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Message<List<Set<String>>> msg = null;
		int times = 1000 * 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			msg = Jsons.readGeneric(json, Message.class, List.class, Set.class, String.class);
		}
		long end = System.currentTimeMillis();

		System.out.println("used time:" + (end - start) + "-->" + Jsons.writeAsString(msg));
	}
}
