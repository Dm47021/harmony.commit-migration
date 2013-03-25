package fr.labri.harmony.analysis.migration.scanlib;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import fr.labri.seutils.structure.Pair;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.THashSet;

public class MigrationComputing {

	public static void main(String[] args) throws IOException {
		String directory = "data";


		String dbName = directory+"/projets-migrants";

		System.out.println(dbName);

		Configuration cfg = new Configuration();
		Properties props = new Properties();
		props.setProperty("hibernate.connection.driver_class",
				"org.h2.Driver");
		props.setProperty("hibernate.connection.url", "jdbc:h2:" + dbName
				+ ";LOCK_TIMEOUT=100000");
		props.setProperty("hibernate.connection.username", "SA");
		props.setProperty("hibernate.connection.password", "");
		props.setProperty("hibernate.dialect",
				"org.hibernate.dialect.H2Dialect");
		props.setProperty("hibernate.hbm2ddl.auto", "update");
		props.setProperty("hibernate.current_session_context_class",
				"thread");
		props.setProperty("hibernate.jdbc.batch_size", "20");
		cfg.mergeProperties(props);
		cfg.addResource("fr/labri/harmony/analysis/migration/scanlib/Project.hbm.xml");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
		.applySettings(cfg.getProperties()).buildServiceRegistry();
		Session session = cfg.buildSessionFactory(serviceRegistry)
				.openSession();
		List<Project> projects = (List<Project>)session.createQuery("From Project").list();

		System.out.println(projects.size() + " projects");

		long t1 = System.currentTimeMillis();

		Map<Pair<String, String>, Set<String>> projects_score = new HashMap<Pair<String, String>, Set<String>>();
		Map<Pair<String, String>, Set<String>> commits_score = new HashMap<Pair<String, String>, Set<String>>();

		for (Project project : projects) {

			if (project.getSteps().size() > 0) {

				List<ProjectStep> ps = new ArrayList<ProjectStep>(
						project.getSteps());
				Collections.sort(ps, new ProjectStepComparator());
				for (int i = 0; i < ps.size() - 1; i++) {

					Set<String> sources = new HashSet<String>(ps.get(i)
							.getLibraries());
					Set<String> targets = new HashSet<String>(ps.get(i + 1)
							.getLibraries());

					sources.removeAll(ps.get(i + 1).getLibraries());
					targets.removeAll(ps.get(i).getLibraries());

					if ((sources.size() > 7 && targets.size() > 7)
							|| (sources.size() + targets.size()) > 16) {
						// On skip si le produit cartésien est trop gros
					} else {

						for (String source : sources) {
							for (String target : targets) {
								Pair<String, String> p = new Pair<String, String>(
										source, target);
							
								if (!projects_score.containsKey(p))
									projects_score.put(p, new HashSet<String>());


								for (String message : ps.get(i + 1).getCommits()) {
									if (isRelevantMessage(message, source, target)) {
										if (!commits_score.containsKey(p))
											commits_score.put(p, new HashSet<String>());
										commits_score.get(p).add(message);
									}
								}

								projects_score.get(p).add(project.getUrl());
							}
						}
					}

				}
			}

		}

		Set<Integer> v1 = new THashSet<>();
		for(Set<String> proj : projects_score.values())
			v1.add(proj.size());
		TIntArrayList v2 = new TIntArrayList();
		v2.addAll(v1);
		v2.sort();
		v2.reverse();
		TIntIterator it = v2.iterator();
		StringBuffer buff = new StringBuffer();
		StringBuffer buff_csv = new StringBuffer();

		buff.append("<form name=\"test\">");
		buff.append("<tr><td>Source</td><td>Target</td><td>Score</td><td>Logs</td><td>Form</td></tr>");

		int rules_found = 0;
		int rules_not_found = 0;
		int rules_not_correct = 0;

		while (it.hasNext()) {
			int value = it.next();
			for (Pair<String, String> pair : projects_score.keySet()) {
				if (projects_score.get(pair).size() == value) {
					boolean correct = GetTrueRules.isTrueRule(pair);
					buff.append("<tr><td>" + pair.getFirst() + "</td><td>"
							+ pair.getSecond() + "</td><td>" + value
							+ "</td><td>" + correct + "</td><td>");
					buff_csv.append(pair.getFirst() + ";" + pair.getSecond()
							+ ";" + value + "\n");
					if(commits_score.containsKey(pair)) {
						for (String commit : commits_score.get(pair)) {
							buff.append(commit + "<hr>");
						}
					}
					buff.append("</td></tr>");
					if (correct && commits_score.containsKey(pair))
						rules_found++;
					else if (correct && !commits_score.containsKey(pair)) {
						rules_not_found++;
					} else if (!correct && commits_score.containsKey(pair)) {
						rules_not_correct++;
					}
				}
			}
		}

		buff.insert(
				0,
				"<html><head><meta http-equiv='Content-Type' content='text/html; charset=windows-1252' /></head><body> "
						+ projects.size()
						+ " Projects <hr><br><table border='1'>");
		buff.append("</table>");
		buff.append("<div id='result'></div>");
		buff.append("</body></html>");

		FileWriter fw = new FileWriter("migration-rules.html");
		fw.write(buff.toString());
		fw.close();

		FileWriter fw2 = new FileWriter("migration-rules.csv");
		fw2.write(buff_csv.toString());
		fw2.close();

		long t2 = System.currentTimeMillis();

		System.out.println(t2 - t1 + " milliseconds");
		
		System.out.println(projects_score.keySet().size()+" rules");
		System.out.println("********** Results with commits logs ");
		System.out.println("Rules Correct Detected " + rules_found);
		System.out.println("Rules Correct Not Found " + rules_not_found);
		System.out.println("Rules Not Correct Detected " + rules_not_correct);

	}

	private static boolean isRelevantMessage(String message, String source,
			String target) {
		
		//return basicCompare(message, source, target);
		return tokenCompare(message, source, target);
	}

	private static boolean basicCompare(String message, String source,
			String target) {
		message = message.toLowerCase();

		if (message.contains(source) && message.contains(target)) {
			return true;
		}
		return false;
	}

	// commons-collections & guava
	// on va chercher dans le message commons&guava OU collections&guava
	private static boolean tokenCompare(String message, String source,
			String target) {
		message = message.toLowerCase();

		source = source.replaceAll("\\.", "\\-");
		source = source.replaceAll("\\_", "\\-");
		String ts[] = source.split("\\-");

		target = target.replaceAll("\\.", "\\-");
		target = target.replaceAll("\\_", "\\-");
		String tt[] = target.split("\\-");

		boolean $1 = false;
		boolean $2 = false;

		for (String t : ts) {
			if (message.contains(t)) {
				$1 = true;
			}
		}
		for (String t : tt) {
			if (message.contains(t)) {
				$2 = true;
			}
		}
		if ($1 && $2) {
			return true;
		}
		return false;
	}

}
