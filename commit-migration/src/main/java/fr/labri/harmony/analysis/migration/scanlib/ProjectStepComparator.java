package fr.labri.harmony.analysis.migration.scanlib;

import java.util.Comparator;

public class ProjectStepComparator implements Comparator<ProjectStep>{

	@Override
	public int compare(ProjectStep o1, ProjectStep o2) {
		if( o1.getStep() > o2.getStep())
			return 1;
		if( o1.getStep() < o2.getStep())
			return -1;
		return 0;
	}

}
