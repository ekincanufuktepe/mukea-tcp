package mukea.tcp.ea;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Iterator;
import org.opt4j.core.Individual;
import org.opt4j.core.IndividualFactory;
import org.opt4j.core.optimizer.IndividualCompleter;
import org.opt4j.core.optimizer.IterativeOptimizer;
import org.opt4j.core.optimizer.Population;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.start.Constant;
import org.opt4j.operators.crossover.Crossover;
import org.opt4j.operators.mutate.Mutate;
import org.opt4j.optimizers.ea.Mating;
import org.opt4j.optimizers.ea.Selector;

import com.google.inject.Inject;

/**
 * The {@link EvolutionaryAlgorithm} is an implementation of an Evolutionary
 * Algorithm based on the operators {@link Crossover} and {@link Mutate}. It
 * uses a {@link Selector} for the mating and environmental selection.
 * 
 */

public class EvolutionaryAlgorithmWithLocalSearch implements IterativeOptimizer{

	protected int alpha;

	protected int lambda;

	protected int mu;

	protected Selector selector;

	protected Mating mating;

	private IndividualFactory individualFactory;

	private Population population;

	private IndividualCompleter completer;
	
	protected int onlyKillerTestCount;

	/**
	 * Constructs an {@link EvolutionaryAlgorithm} with a {@link Population}, an
	 * {@link IndividualFactory}, a {@link IndividualCompleter}, a
	 * {@link Selector}, a {@link Mating}, the number of generations, the
	 * population size, the number of parents, the number of offspring, and a
	 * random number generator.
	 * 
	 * @param population
	 *            the population
	 * @param individualFactory
	 *            the individual factory
	 * @param completer
	 *            the completer
	 * @param selector
	 *            the selector
	 * @param mating
	 *            the mating
	 * @param alpha
	 *            the population size
	 * @param mu
	 *            the number of parents
	 * @param lambda
	 *            the number of offspring
	 */
	private Individual i;
	public EvolutionaryAlgorithmWithLocalSearch(Individual i) {
		this.setI(i);
	}
	
	@Inject
	public EvolutionaryAlgorithmWithLocalSearch(
			Population population,
			IndividualFactory individualFactory,
			IndividualCompleter completer,
			Selector selector,
			Mating mating,
			@Constant(value = "alpha", namespace = EvolutionaryAlgorithmWithLocalSearch.class) int alpha,
			@Constant(value = "mu", namespace = EvolutionaryAlgorithmWithLocalSearch.class) int mu,
			@Constant(value = "lambda", namespace = EvolutionaryAlgorithmWithLocalSearch.class) int lambda,
			@Constant(value = "onlyKillerTestCount", namespace = EvolutionaryAlgorithmWithLocalSearch.class) int onlyKillerTestCount) {
		this.selector = selector;
		this.mating = mating;
		this.alpha = alpha;
		this.mu = mu;
		this.lambda = lambda;
		this.individualFactory = individualFactory;
		this.population = population;
		this.completer = completer;
		this.onlyKillerTestCount = onlyKillerTestCount;

		if (alpha <= 0) {
			throw new IllegalArgumentException("Invalid alpha: " + alpha);
		}

		if (mu <= 0) {
			throw new IllegalArgumentException("Invalid mu: " + mu);
		}

		if (lambda <= 0) {
			throw new IllegalArgumentException("Invalid lambda: " + lambda);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.optimizer.IterativeOptimizer#initialize()
	 */
	public void initialize() {
		selector.init(alpha + lambda);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.optimizer.IterativeOptimizer#next()
	 */
	public void next() throws TerminationException {
		
		// remove lames
		if (population.size() > alpha) {
			Collection<Individual> lames = selector.getLames(population.size()
					- alpha, population);
			population.removeAll(lames);
		}
		// set the number of new offspring individuals per generation
		int offspringCount = lambda;

		// 1) fill the population until it reaches the size alpha
		while (population.size() < alpha && offspringCount > 0) {
			population.add(individualFactory.create());
			offspringCount--;
		}

		// 2) generate offspring by mating
		if (offspringCount > 0) {
			if (offspringCount < lambda) { // evaluate new individuals first
				completer.complete(population);
			}

			Collection<Individual> parents = selector
					.getParents(mu, population);
			Collection<Individual> offspring = mating.getOffspring(
					offspringCount, parents);
			population.addAll(offspring);
		}
		
		Iterator<Individual> it = population.iterator();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		// Create threads for each offspring for local search
		while(it.hasNext()) {
			Individual i = it.next();
			LocalSearch ls = new LocalSearch(i, onlyKillerTestCount);
			Thread t = new Thread(ls);
			t.start();
			threads.add(t);
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Individual getI() {
		return i;
	}

	public void setI(Individual i) {
		this.i = i;
	}
}