package mkea.tcp.ea;

import org.opt4j.core.problem.Decoder;
import org.opt4j.core.genotype.PermutationGenotype;

public class TCPDecoder implements Decoder<PermutationGenotype<Integer>, TCPOrder> {
	public TCPOrder decode(PermutationGenotype<Integer> genotype) {
		TCPOrder order = new TCPOrder();
		for(Integer i : genotype) {
			order.add(i);
		}
		return order;
	}
	
}
