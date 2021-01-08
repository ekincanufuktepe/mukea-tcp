package mkea.tcp.importTestReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class APMKCalculator {
	
	public APMKCalculator() {
	
	}
	
	
	// Calculate APMK (Average Percentage Mutant Killed), which is also the fitness function
	public Double APMK(HashMap<MutantInfo, Set<String>> mutantTestInfo, ArrayList<String> orderedTestCases) {
		
		Double apmkScore = 0.0;
		double indexSum = 0.0;
		int killedMutant = 0;
		for(MutantInfo m : mutantTestInfo.keySet()) {
			
			if(findMinIndex(mutantTestInfo.get(m), orderedTestCases) != 0) {
				indexSum = indexSum + findMinIndex(mutantTestInfo.get(m), orderedTestCases);
				killedMutant++;
			}
		}
		
		double denominator = (double) (orderedTestCases.size()*killedMutant);
		
		apmkScore = 1.0 - (indexSum/denominator) + (1/(double)(2*orderedTestCases.size()));

		return apmkScore*100.0;
	}
	
	// Find the mutant killer test case with the least index
	private int findMinIndex(Set<String> killerTestCases, ArrayList<String> orderedTestCases) {
		for(int i=0; i<orderedTestCases.size(); i++) {
			if(killerTestCases.contains(orderedTestCases.get(i))) {
				return (i+1);
			}
		}
		return orderedTestCases.size();
	}

}
