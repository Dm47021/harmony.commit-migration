package fr.labri.harmony.analysis.migration.scanlib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.labri.utils.collections.Pair;

public class GetTrueRules {

	static Set<String> rules = new HashSet<>();

	static {
		try {
			List<String> lines = Files.readAllLines(Paths.get("data/TrueRules.csv"), Charset.forName("UTF-8"));
			for(String line : lines) {
				String tk[] = line.split("\\;");
				rules.add(tk[0]+";"+tk[1]+":"+tk[2]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static boolean isTrueRule(Pair<String,String> rule) {

		for(String r : rules) {
			String tk[] = r.split("\\:");
			if(tk[0].equals(rule.getFirst()+";"+rule.getSecond()))
				return true;
		}

		return false;
	}

}
