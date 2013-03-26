package fr.labri.harmony.analysis.migration.scanlib;



import java.util.ArrayList;
import java.util.List;

public class Project {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (id != other.id)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	private int id;
	private String url;
	
	private List<ProjectStep> steps;
	
	public Project() {
		steps = new ArrayList<ProjectStep>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ProjectStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ProjectStep> steps) {
		this.steps = steps;
	}

	
}
