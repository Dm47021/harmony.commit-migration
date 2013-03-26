package fr.labri.harmony.analysis.migration.scanlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;

public class AddGoogleInfo {

	public static void main(String[] args) throws IOException, InterruptedException {
		ObjectMapper map = new ObjectMapper();
		String baseUrl = "https://www.googleapis.com/customsearch/v1?key=" + args[0] + "&cx=" + args[1] + "&q=";
		ArrayNode rules = (ArrayNode) map.readTree(new File("data/migration-rules-clean-active.json"));
		System.out.println(rules.size());
		for (int i = 0; i < rules.size(); i++) {
			JsonNode rule = rules.get(i);
			ObjectNode metadata = (ObjectNode) rule.get("metadata");
			System.out.print(i + " ");
			if (!metadata.has("google-migration")) {
				ArrayNode google = JsonNodeFactory.instance.arrayNode();
				String source = rule.get("source").asText();
				String target = rule.get("target").asText();
				String query = source + "%20" + target + "%20migration";		
				URL url = new URL(baseUrl + query);
				System.out.print("performing " + url.toString() + "\n");
				URLConnection c = url.openConnection();
				BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
				ObjectMapper m = new ObjectMapper();
				JsonNode root = m.readTree(r);
				long nbItems = Long.parseLong(root.get("searchInformation").get("totalResults").asText());
				ArrayNode items = (ArrayNode) root.get("items");
				if (nbItems > 0) for (int j = 0; j < items.size(); j++) google.add(items.get(j).get("snippet").asText());
				metadata.put("google-migration", google);
			} else System.out.print("done\n");
			map.writeValue(new File("data/migration-rules-clean-active.json"), rules);
		}
	}

	public static String txt(String html) {
		return Jsoup.parse(html).text();
	}
}
