package fr.labri.harmony.analysis.migration.scanlib;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;

public class ComputeGoogleRules {

	public static void main(String[] args) throws IOException, InterruptedException {
		ObjectMapper map = new ObjectMapper();
		ArrayNode rules = (ArrayNode) map.readTree(new File("data/migration-rules-clean-active.json"));
		for (int i = 0; i < rules.size(); i++) {
			JsonNode rule = rules.get(i);
			String source = rule.get("source").asText();
			String target = rule.get("target").asText();
			boolean correct = rule.get("correct").asBoolean();
			boolean found = false;
			ObjectNode metadata = (ObjectNode) rule.get("metadata");
			if (metadata.has("google-migration")) {
				ArrayNode google = (ArrayNode) metadata.get("google-migration");
				int occur = 0;
				for(int j = 0; j < google.size(); j++) {
					String[] units = google.get(j).asText().toLowerCase().split("\\.\\.\\.");
					for (String snippet: units) {
						if ( /* snippet.contains("migrat") && */ snippet.contains(source) && 
								snippet.contains(target) ) {
							occur++;
							//System.out.println(snippet);
						}
					}
				}
				if ( occur > 1 ) {
					found = true;
					
				}
			}
			System.out.println(source + " - " + target + " - " + correct  + " - " + found);
		}
	}

	public static String txt(String html) {
		return Jsoup.parse(html).text();
	}
}
