package fr.labri.harmony.analysis.migration.scanlib;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.labri.seutils.structure.Pair;


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
			// TODO Auto-generated catch block
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

	public static void main(String[] args) throws IOException {
		String dir = "/home/cteyton/Documents/travail/Projets/MigrationAnalysis/JSPE/";

		String filename="collections_score";
		FileWriter fw = new FileWriter(dir+filename+".csv");
		List<String> libs = Arrays.asList("hppc","lambdaj","colt","trove4j","mahout","guava","commons-collections","javolution","lucene","collections-generic");
		fw.write("A;B;C\n");
		for(String source : libs) {
			for(String target : libs) {
				for(String rule : rules) {
					String tk[] = rule.split("\\:");
					if(tk[0].equals(source+";"+target)){
						System.out.println(tk[0]+";"+tk[1]);
						fw.write(tk[0]+";"+tk[1]+"\n");
					}
				}
			}
		}
		fw.close();
	}

}
