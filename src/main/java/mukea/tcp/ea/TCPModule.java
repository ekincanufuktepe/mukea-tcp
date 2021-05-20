package mukea.tcp.ea;

import java.util.ArrayList;

import org.opt4j.core.problem.ProblemModule;

import mukea.tcp.Main;

public class TCPModule extends ProblemModule {
	
	protected ArrayList<String> tests;
	public TCPModule() {
		this.tests = Main.testCases;
	}
	
	public ArrayList<String> getTests() {
		return tests;
	}

	public void setTests(ArrayList<String> tests) {
		this.tests = tests;
	}
	
	@Override
	protected void config() {
		bindProblem(TCPCreator.class, TCPDecoder.class, TCPEvaluator.class);
	}
	
}
