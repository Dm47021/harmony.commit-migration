package fr.labri.harmony.analysis.migration.scanlib;


import java.util.HashSet;
import java.util.Set;

public class ProjectStep {

	private int id;
	private long timestamp;
	private int step;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProjectStep() {
		this.libraries = new HashSet<String>();
		this.imports = new HashSet<String>();
		this.commits = new HashSet<String>();
	}

	public Set<String> getLibraries() {
		return libraries;
	}

	public void setLibraries(Set<String> libraries) {
		this.libraries = libraries;
	}

	private Set<String> libraries;
	private Set<String> imports;
	private Set<String> commits;

	public Set<String> getCommits() {
		return commits;
	}

	public void setCommits(Set<String> commits) {
		this.commits = commits;
	}

	public Set<String> getImports() {
		return imports;
	}

	public void setImports(Set<String> imports) {
		this.imports = imports;
	}

}
