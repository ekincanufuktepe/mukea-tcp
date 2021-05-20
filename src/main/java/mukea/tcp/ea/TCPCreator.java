package mukea.tcp.ea;

import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

import java.util.Collections;

import org.opt4j.core.genotype.PermutationGenotype;

public class TCPCreator implements Creator<PermutationGenotype<Integer>>{

	protected final TCPProblem problem;

	public static boolean flag = false;
	
	@Inject
	public TCPCreator(TCPProblem problem) {
		this.problem = problem;
	}

	public PermutationGenotype<Integer> create() {
		PermutationGenotype<Integer> genotype = new PermutationGenotype<Integer>();
		for(Integer testID : problem.getTestCases()) {
			genotype.add(testID);
		}
		if(flag){
			Collections.shuffle(genotype);
		}
		flag = true;
		return genotype;
	}

}
