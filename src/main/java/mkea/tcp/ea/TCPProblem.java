package mkea.tcp.ea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mkea.tcp.Main;
import mkea.tcp.importTestReport.MutantInfo;
import mkea.tcp.importTestReport.MutationKilledByTestParser;


public class TCPProblem {
	protected Set<Integer> testCases = new HashSet<Integer>();
	protected static HashMap<String,Integer> testCasesMap = new HashMap<String, Integer>();
	
	public TCPProblem() {
		for(int i=0; i<Main.testCases.size(); i++) {
			testCasesMap.put(Main.testCases.get(i),(i+1));		
			MutationKilledByTestParser.testIDNameMap.put(i+1, Main.testCases.get(i));
			testCases.add(i+1);
		}
		int mutID = 0;
		for(MutantInfo m : MutationKilledByTestParser.mutantMap.keySet())
		{
			MutationKilledByTestParser.intMutantMap.put(mutID, new HashSet<Integer>());
			for(String test: MutationKilledByTestParser.mutantMap.get(m))
			{
				MutationKilledByTestParser.intMutantMap.get(mutID).add(testCasesMap.get(test));
			}
			mutID++;
		}
//		testCases = testCasesMap.keySet();
	}

	public Set<Integer> getTestCases() {
		return testCases;
	}

	public void setTestCases(Set<Integer> testCases) {
		this.testCases = testCases;
	}
	

}
