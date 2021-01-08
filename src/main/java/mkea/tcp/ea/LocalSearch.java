package mkea.tcp.ea;

import java.util.Random;

import org.opt4j.core.DoubleValue;
import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.genotype.PermutationGenotype;

public class LocalSearch extends Thread  {

	private Individual i;

	private int onlyKillerTestCount;

	public LocalSearch(Individual i, int onlyKillerTestCount) {
		this.i = i;
		this.onlyKillerTestCount = onlyKillerTestCount;
	}

	@Override
	public void run() {
		try { 
			hillClimbingLocalSearch(i);
		} 
		catch (Exception e) { 
			// Throwing an exception 
			System.out.println ("Exception is caught: "+e.getLocalizedMessage()); 
		} 
	}

	private void hillClimbingLocalSearch(Individual i) {
		Genotype g = i.getGenotype();
		@SuppressWarnings("unchecked")
		PermutationGenotype<Integer> genotype = (PermutationGenotype<Integer>) g;
		Double apmk;
		if ( i.isEvaluated() )
			apmk = ((DoubleValue) i.getObjectives().getValues().toArray()[0]).getDouble();
		else  
			apmk = TCPEvaluator.APMK(genotype);
		Double newApmk = 0.0;
		Random rand = new Random();
		int iterations = 0;
		int index1 = 0;
		int index2 = 0;
		while(iterations<20 /*|| !improved*/) {
			while(index1 == index2) {
				index1 = rand.nextInt(onlyKillerTestCount);
				index2 = rand.nextInt(onlyKillerTestCount);    
			}
			Integer tmp = genotype.get(index1);
			genotype.set(index1, genotype.get(index2));
			genotype.set(index2, tmp);
			newApmk = TCPEvaluator.APMK(genotype);

			if(newApmk < apmk) {
				tmp = genotype.get(index1);
				genotype.set(index1, genotype.get(index2));
				genotype.set(index2, tmp);
			}
			else {
				apmk = newApmk;
			}
			iterations++;
			index1 = 0;
			index2 = 0;
		}

		Objectives o =  new Objectives();
		o.add("apmk", Sign.MAX, apmk);
		i.setObjectives(o);
	}

}
