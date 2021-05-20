package mukea.tcp;

import java.util.ArrayList;
import java.util.Collections;

import org.opt4j.core.Individual;
import org.opt4j.core.common.completer.IndividualCompleterModule;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.start.Opt4JTask;
import org.opt4j.operators.crossover.BasicCrossoverModule;
import org.opt4j.operators.mutate.BasicMutateModule;
import org.opt4j.operators.mutate.BasicMutateModule.MutationRateType;
import org.opt4j.operators.mutate.BasicMutateModule.PermutationType;

import mukea.tcp.ea.EvolutionaryAlgorithmWithLocalSearchModule;
import mukea.tcp.ea.TCPCreator;
import mukea.tcp.ea.TCPModule;
import mukea.tcp.importTestReport.APMKCalculator;
import mukea.tcp.importTestReport.MutationKilledByTestParser;

public class Main {

	public static ArrayList<String> testCases;

	public static void main(String[] args) {
		// Set the mutation report file
		MutationKilledByTestParser parser = new MutationKilledByTestParser("mutantKillReports/commons-csv/mutations.xml");
		// MutationKilledByTestParser parser = new MutationKilledByTestParser("mutantKillReports/jsoup/mutations.xml");
		// MutationKilledByTestParser parser = new MutationKilledByTestParser("mutantKillReports/commons-cli/mutations.xml");
		// MutationKilledByTestParser parser = new MutationKilledByTestParser("mutantKillReports/commons-codec/mutations.xml");
		// MutationKilledByTestParser parser = new MutationKilledByTestParser("mutantKillReports/jackson-core/mutations.xml");
		APMKCalculator apmk = new APMKCalculator();
		long startTime = System.nanoTime();

		/**
		 * To Run Total Technique (TMKPT) uncomment line below
		 * TMKPT: Order Test cases by most killing to least killing
		 */
		//			ArrayList<String> sortedTestsByMaxKill =parser.calculateTestMutantKillCountTotal();

		/**
		 * To Run Additional Additionl (AMKPT) uncomment line below
		 * AMKPT: Order Test cases by most killing to least killing mutants that are not killed yet
		 */
		//			ArrayList<String> sortedTestsByMaxKill = parser.orderTestCasesInMostNotKilledYet();

		/**
		 * To Generate a Random order of test cases uncomment the 2 lines below
		 */
		Collections.shuffle(parser.getTestCases());
		ArrayList<String> sortedTestsByMaxKill = parser.getTestCases();

		long endTime1 = System.nanoTime();
		long timeElapsed1 = endTime1 - startTime;

		testCases = sortedTestsByMaxKill;

		/**
		 * This is just a test code to see how long the does it take
		 * to find all permutations O(n!).
		 * 
		 * Note: Not a good idea :)
		 */
		/*
			OptimalTCP opt = new OptimalTCP(testCases, parser.getMutantMap());
			opt.permute();
			long endTime1 = System.nanoTime();
			long timeElapsed1 = endTime1 - startTime;
			System.out.println("Time: "+(timeElapsed1 / 1000000000.0));
		 */
		//
		/**
		 * Evolutionary Algorithm (EA)
		 * Uncomment the code below to use the pure EA, without local search
		 * */
		//			EvolutionaryAlgorithmModule ea = new EvolutionaryAlgorithmModule();

		/**
		 * Local Search Augmented Evolutionary Algorithm (EA-LS)
		 * Uncomment the code below to use the EA-LS
		 * */
		// Activate Local search augmented evolutionary algorithm
		EvolutionaryAlgorithmWithLocalSearchModule ea = new EvolutionaryAlgorithmWithLocalSearchModule();
		// Activate the adaptive local search space, if not used the local search space will be set the size of the test suite. 
		ea.setOnlyKillerTestCount(parser.getZeroKillerCount());

		ea.setGenerations(100);
		ea.setCrossoverRate(0.75);
		TCPModule dtlz = new TCPModule();

		BasicMutateModule mm = new BasicMutateModule();
		mm.setMutationRate(0.3);
		MutationRateType mtrMutationRateType = MutationRateType.ADAPTIVE;
		mm.setMutationRateType(mtrMutationRateType);
		PermutationType pt = PermutationType.INSERT;
		mm.setPermutationType(pt);

		TCPCreator.flag = false;
		BasicCrossoverModule bcm = new BasicCrossoverModule();
		bcm.setPermutationType(org.opt4j.operators.crossover.BasicCrossoverModule.PermutationType.BUCKET);

		IndividualCompleterModule icm = new IndividualCompleterModule();
		icm.setThreads(12);
		icm.setType(IndividualCompleterModule.Type.PARALLEL);

		//			ViewerModule viewer = new ViewerModule();
		//			viewer.setCloseOnStop(false);
		Opt4JTask task = new Opt4JTask(false);

		//			task.init(ea, dtlz, mm, icm, bcm, viewer);
		task.init(ea, dtlz, mm, icm, bcm);

		try {
			task.execute();
			Archive archive = task.getInstance(Archive.class);
			for (Individual individual : archive) {
				long endTime2 = System.nanoTime();
				long timeElapsed2 = endTime2 - startTime;
				//					System.out.println(individual.getObjectives().getValues().toArray()[0]+"\t"+(timeElapsed2 / 1000000000.0));
				System.out.println(apmk.APMK(parser.getMutantMap(), sortedTestsByMaxKill)+"\t"+(timeElapsed1 / 1000000000.0)+"\t"+individual.getObjectives().getValues().toArray()[0]+"\t"+(timeElapsed2 / 1000000000.0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			task.close();
		}
	}

	/**
	 * If you want to view the test orders with the actual names,
	 * you need to use the method below.
	 */
	/*
	private static void convertToPhenotype(String genotype) {
		String ids[] = genotype.split(" ");
		for(int i=0; i<ids.length; i++) {
			if(ids[i].contains(",")) {
				ids[i] = ids[i].substring(0, ids[i].length()-1);
			}
			if(ids[i].contains("]")) {
				ids[i] = ids[i].substring(0, ids[i].length()-1);
			}
			if(ids[i].contains("[")) {
				ids[i] = ids[i].substring(1, ids[i].length());
			}
			System.out.println("Index: "+ids[i]+", Test: "+MutationKilledByTestParser.testIDNameMap.get(Integer.parseInt(ids[i])));
		}
	}
	 */
}
