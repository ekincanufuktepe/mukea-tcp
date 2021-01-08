package mkea.tcp.ea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import mkea.tcp.importTestReport.MutationKilledByTestParser;

public class TCPEvaluator implements Evaluator<TCPOrder> {

	Objective apmk = new Objective("apmk", Sign.MAX);

	public Objectives evaluate(TCPOrder testCaseOrder) {
		Objectives objectives = new Objectives();
		objectives.add(apmk, APMK(testCaseOrder));

		return objectives;
	}

	//String Map version
	/***
	public Double APMK(ArrayList<Integer> orderedTestCases) {

		Double apmkScore = 0.0;
		double indexSum = 0.0;
		for(MutantInfo m : MutationKilledByTestParser.mutantMap.keySet()) {
			indexSum = indexSum + findMinIndex(MutationKilledByTestParser.mutantMap.get(m), orderedTestCases);
		}

		apmkScore = 1.0 - (indexSum/MutationKilledByTestParser.constant1) + MutationKilledByTestParser.constant2;
//		System.out.println(apmkScore);
//		System.out.println(MutationKilledByTestParser.constant1+" --- "+(indexSum/MutationKilledByTestParser.constant1));
//		System.out.println(MutationKilledByTestParser.constant2);
//		System.out.println();
		return apmkScore*100.0;
	} */
	
	// Integer Map Version
	public static Double APMK(ArrayList<Integer> orderedTestCases) {

		Double apmkScore = 0.0;
		double indexSum = 0.0;
		for(Integer m : MutationKilledByTestParser.intMutantMap.keySet()) {
			indexSum = indexSum + findMinIndex(MutationKilledByTestParser.intMutantMap.get(m), orderedTestCases);
		}

		apmkScore = 1.0 - (indexSum/MutationKilledByTestParser.constant1) + MutationKilledByTestParser.constant2;
//		System.out.println(apmkScore);
//		System.out.println(MutationKilledByTestParser.constant1+" --- "+(indexSum/MutationKilledByTestParser.constant1));
//		System.out.println(MutationKilledByTestParser.constant2);
//		System.out.println();
		return apmkScore*100.0;
	}


	// String mapping version
	// Find the mutant killer test case with the least index
	/**
	private int findMinIndex(Set<String> killerTestCases, ArrayList<Integer> orderedTestCases) {
//		Integer minIndex = null;
//		for(String test : killerTestCases) {
//			if(minIndex == null) {
//				minIndex = orderedTestCases.indexOf(TCPProblem.testCasesMap.get(test));
//			}
//			else {
//				if (minIndex > orderedTestCases.indexOf(TCPProblem.testCasesMap.get(test))) {
//					minIndex = orderedTestCases.indexOf(TCPProblem.testCasesMap.get(test));
//				}
//			}
//		}
		
		Set<Integer> testIntMap = new HashSet<Integer>();
		for(String test : killerTestCases) {
			testIntMap.add(TCPProblem.testCasesMap.get(test));
		}
		for(int i=0; i<orderedTestCases.size(); i++) {
			if(testIntMap.contains(orderedTestCases.get(i))) {
				return (i+1);
			}
		}
		System.out.println("SOMETHING WENT WRONG_b");
		return orderedTestCases.size();
	} */
	
	// Integer mapping version
	// Find the mutant killer test case with the least index
	private static int findMinIndex(Set<Integer> killerTestCases, ArrayList<Integer> orderedTestCases) {	
		for(int i=0; i<orderedTestCases.size(); i++) {
			if(killerTestCases.contains(orderedTestCases.get(i))) {
				return (i+1);
			}
		}
		System.out.println("SOMETHING WENT WRONG_b");
		return orderedTestCases.size();
	}

	public List<Objective> getObjectives() {
		return Arrays.asList(apmk);
	}

}
