package fr.labri.harmony.analysis.migration.scanlib;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;

public class ComputeGoogleRules {

	public static void main(String[] args) throws IOException, InterruptedException {
		ObjectMapper map = new ObjectMapper();
		ArrayNode rules = (ArrayNode) map.readTree(new File("data/MigrationRules.json"));
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
					String[] texts = google.get(j).asText().toLowerCase().split("\\.\\.\\.");
					for (String text: texts) if (isMigration(text, source, target)) occur++;
				}
				if ( occur > 0 ) found = true;
			}
			if (found == true) System.out.println(source + " - " + target + " - " + correct  + " - " + found);
		}
	}
	
	public static boolean isMigration(String text, String source, String target) {
		for (String regexp: regexps(source, target)) if (text.matches(regexp)) {
			System.out.println(text);
			System.out.println(Arrays.toString(regexps(source, target)));
			return true;
		}
		return false;
	}
	
	public static String[] regexps(String source, String target) {
		return new String[] {
			".*migrat.*" + source + ".*\\s.*" + target + ".*",
			".*replac.*" + source + ".*\\s.*" + target + ".*",
			".*from.*" + source + ".*to.*" + target + ".*",
		};
	}

	public static String txt(String html) {
		return Jsoup.parse(html).text();
	}
}
