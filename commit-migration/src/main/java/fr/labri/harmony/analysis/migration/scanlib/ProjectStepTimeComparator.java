package fr.labri.harmony.analysis.migration.scanlib;

import java.util.Comparator;

public class ProjectStepTimeComparator implements Comparator<ProjectStep>{

	@Override
	public int compare(ProjectStep o1, ProjectStep o2) {
		if( o1.getTimestamp() > o2.getTimestamp())
			return 1;
		if( o1.getTimestamp() < o2.getTimestamp())
			return -1;
		return 0;
	}

}
