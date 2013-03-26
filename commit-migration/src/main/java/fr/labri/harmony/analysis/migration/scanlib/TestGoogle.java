package fr.labri.harmony.analysis.migration.scanlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.jsoup.Jsoup;

public class TestGoogle {

	public static void main(String[] args) throws IOException {
		String surl = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&q=";
		surl += "log4j%20junit%20replace";
		URL url = new URL(surl);
		URLConnection c = url.openConnection();
		BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
		ObjectMapper m = new ObjectMapper();
		JsonNode root = m.readTree(r);
		ArrayNode res = (ArrayNode) root.get("responseData").get("results");
		for (int i = 0; i < res.size(); i++) {
			JsonNode content = res.get(i).get("content");
			System.out.println(txt(content.asText()));
		}
	}
	
	public static String txt(String html) {
	    return Jsoup.parse(html).text();
	}
}
