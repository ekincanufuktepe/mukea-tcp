package mukea.tcp.importTestReport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MutationKilledByTestParser {

	private int onlyKillerTestCount = 0;
	public static HashMap<MutantInfo, Set<String>> mutantMap;
	public static HashMap<Integer, Set<Integer>> intMutantMap;
	public static HashMap<MutantInfo, Set<String>> mutantMapCopy;
	private HashMap<String, Integer> testMutantKillCount;
	public static HashMap<Integer,String> testIDNameMap = new HashMap<Integer, String>();
	private String mutationReportFile;
	private ArrayList<String> testCases;
	private ArrayList<String> testCasesAdditionalOrder;
	public static double constant1;
	public static double constant2;

	public MutationKilledByTestParser(String mutationReportFile) {
		this.mutationReportFile = mutationReportFile;
		testCasesAdditionalOrder = new ArrayList<String>();
		mutantMap = new HashMap<MutantInfo, Set<String>>();
		mutantMapCopy = new HashMap<MutantInfo, Set<String>>();
		intMutantMap = new HashMap<Integer, Set<Integer>>();
		testCases = new ArrayList<String>();
		testMutantKillCount = new HashMap<String, Integer>();
		readMutationReport();
		collectTestCases();
		onlyKillerTestCount = testCases.size();
		constant1 = (double) (testCases.size()*mutantMap.keySet().size());
		constant2 = (1.0/((double)2.0*testCases.size()));
	}

	public HashMap<MutantInfo, Set<String>> getMutantMap() {
		return mutantMap;
	}

	public void setMutantMap(HashMap<MutantInfo, Set<String>> mutantMap) {
		MutationKilledByTestParser.mutantMap = mutantMap;
	}

	public ArrayList<String> getTestCases() {
		return testCases;
	}

	public void setTestCases(ArrayList<String> testCases) {
		this.testCases = testCases;
	}

	private void collectTestCases() {
		for(MutantInfo m : mutantMap.keySet()) {
			for(String test : mutantMap.get(m)) {
				if(!testCases.contains(test))
					testCases.add(test);
			}
		}
	}

	public void readMutationReport() {
		String reportPath = mutationReportFile;
		try {
			File fXmlFile = new File(reportPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("mutation");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String mutatedClassName = eElement.getElementsByTagName("mutatedClass").item(0).getTextContent();
					String mutatedMethodName = eElement.getElementsByTagName("mutatedMethod").item(0).getTextContent();
					String mutatedMethodDescriptionName = eElement.getElementsByTagName("methodDescription").item(0).getTextContent();
					String mutatedLineNumber = eElement.getElementsByTagName("lineNumber").item(0).getTextContent();
					String mutationType = eElement.getElementsByTagName("mutator").item(0).getTextContent();

					// Extract Killed mutants with its test cases
					if(eElement.getAttribute("status").equals("KILLED")) {
						MutantInfo mutant = new MutantInfo(mutatedClassName, mutatedMethodName, mutatedMethodDescriptionName, mutatedLineNumber, mutationType);
						boolean mutantFlag = true;
						for(MutantInfo m : mutantMap.keySet()) {
							if(m.equals(mutant)) {
								mutant = m;
								mutantFlag = false;
								break;
							}
						}
						if(mutantFlag) {
							mutantMap.put(mutant, new HashSet<String>());
						}
						String killingTestMethods = eElement.getElementsByTagName("killingTests").item(0).getTextContent();
						if(!killingTestMethods.equals("")) {
							String tests[] = killingTestMethods.split("\\|");
							for(int i=0; i<tests.length; i++) {
								mutantMap.get(mutant).add(tests[i]);
							}
						}

						String survivingTestMethods = eElement.getElementsByTagName("succeedingTests").item(0).getTextContent();
						if(!survivingTestMethods.equals("")) {
							String tests[] = survivingTestMethods.split("\\|");
							for(int i=0; i<tests.length; i++) {
								if(!testCases.contains(tests[i])) {
									testCases.add(tests[i]);
								}
							}
						}
					}
					else if(eElement.getAttribute("status").equals("SURVIVED") || eElement.getAttribute("status").equals("TIMED_OUT")) {
						String survivingTestMethods = eElement.getElementsByTagName("succeedingTests").item(0).getTextContent();
						if(!survivingTestMethods.equals("")) {
							String tests[] = survivingTestMethods.split("\\|");
							for(int i=0; i<tests.length; i++) {
								if(!testCases.contains(tests[i])) {
									testCases.add(tests[i]);
								}
							}
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMutantKillingTestsInfo() {
		for(MutantInfo m : mutantMap.keySet()) {
			System.out.println(m);
			for(String test : mutantMap.get(m)) {
				System.out.println("\t"+test);
			}
		}
		System.out.println(mutantMap.keySet().size());
	}

	public ArrayList<String> orderTestCasesInMostNotKilledYet(){
		mutantMapCopy.putAll(mutantMap);
		ArrayList<String> tests = new ArrayList<String>();
		tests.addAll(testCases);
		while(!mutantMapCopy.isEmpty()) {
			calculateTestMutantKillCountAdditional();
			String test = findMaxKillingTest();
			testCasesAdditionalOrder.add(test);
			removeTestUpdateKillCount(test);
			tests.remove(test);
		}

		Collections.shuffle(tests);
		this.onlyKillerTestCount = tests.size();

		for(String test : tests) {
			testCasesAdditionalOrder.add(test);
		}
		this.onlyKillerTestCount = testCases.size() - onlyKillerTestCount;
		return testCasesAdditionalOrder;
	}

	private void removeTestUpdateKillCount(String testName) {
		ArrayList<MutantInfo> removeMutantList = new ArrayList<MutantInfo>();
		for(MutantInfo mi : mutantMapCopy.keySet()) {
			if(mutantMapCopy.get(mi).contains(testName)) {
				removeMutantList.add(mi);
			}
		}

		// Reset the test kill count based on the test selection for order
		for(String test : testCases) {
			testMutantKillCount.replace(test, 0);
		}

		// Remove the mutants that are already killed
		for(MutantInfo mi : removeMutantList) {
			mutantMapCopy.remove(mi);	
		}
	}

	private String findMaxKillingTest() {
		int killCount = -1;
		ArrayList<String> sameAmountKillers = new ArrayList<String>();
		for(String test : testMutantKillCount.keySet()) {
			if(killCount < testMutantKillCount.get(test)) {
				killCount = testMutantKillCount.get(test);
			}
		}
		for(String test : testMutantKillCount.keySet()) {
			if(killCount == testMutantKillCount.get(test)) {
				sameAmountKillers.add(test);
			}
		}
		Collections.shuffle(sameAmountKillers);
		return sameAmountKillers.get(0);
	}

	private HashMap<String,Integer> calculateTestMutantKillCountAdditional(){
		for(MutantInfo mi : mutantMapCopy.keySet()) {
			for(String testName : mutantMapCopy.get(mi)) {
				if(!testMutantKillCount.containsKey(testName)) {
					testMutantKillCount.put(testName, 1);
				}
				else {
					int killCount = testMutantKillCount.get(testName);
					testMutantKillCount.replace(testName, killCount, killCount+1);
				}
			}
		}
		return testMutantKillCount;
	}

	public ArrayList<String> calculateTestMutantKillCountTotal(){
		HashMap<String,Integer> testMutantKillCountTotal = new HashMap<String, Integer>();
		for(MutantInfo mi : mutantMap.keySet()) {
			for(String testName : mutantMap.get(mi)) {
				if(!testMutantKillCountTotal.containsKey(testName)) {
					testMutantKillCountTotal.put(testName, 1);
				}
				else {
					int killCount = testMutantKillCountTotal.get(testName);
					testMutantKillCountTotal.replace(testName, killCount, killCount+1);
				}
			}
		}
		for(String test : testCases) {
			if(!testMutantKillCountTotal.containsKey(test)) {
				testMutantKillCountTotal.put(test, 0);
			}
		}

		for(String test : testMutantKillCountTotal.keySet()) {
			if(testMutantKillCountTotal.get(test) == 0) {
				onlyKillerTestCount--;
			}
		}

		ArrayList<String> testCaseOrderTotal = new ArrayList<String>();
		while(!testMutantKillCountTotal.isEmpty()) {
			String testName = findMaxKillingTest(testMutantKillCountTotal);
			testCaseOrderTotal.add(testName);
			testMutantKillCountTotal.remove(testName);
		}

		return testCaseOrderTotal;
	}

	private String findMaxKillingTest(HashMap<String, Integer> testMutantKillCountTotal) {
		int killCount = -1;
		ArrayList<String> sameAmountKillers = new ArrayList<String>();
		for(String test : testMutantKillCountTotal.keySet()) {
			if(killCount < testMutantKillCountTotal.get(test)) {
				killCount = testMutantKillCountTotal.get(test);
			}
		}
		for(String test : testMutantKillCountTotal.keySet()) {
			if(killCount == testMutantKillCountTotal.get(test)) {
				sameAmountKillers.add(test);
			}
		}
		Collections.shuffle(sameAmountKillers);

		//		System.out.println("Kill count: "+testMutantKillCountTotal.get(sameAmountKillers.get(0)));
		return sameAmountKillers.get(0);
	}



	public void printTestMutantKillCount() {
		for(String testName : testMutantKillCount.keySet()) {
			System.out.println("Kill: "+testMutantKillCount.get(testName)+", Test: "+testName);
		}
	}

	public HashMap<String, Integer> getTestMutantKillCount() {
		return testMutantKillCount;
	}

	public void setTestMutantKillCount(HashMap<String, Integer> testMutantKillCount) {
		this.testMutantKillCount = testMutantKillCount;
	}

	//sort elements by values  
	public ArrayList<String> sortByValue(final boolean order)   
	{  
		//convert HashMap into List   
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(testMutantKillCount.entrySet());  
		//sorting the list elements  
		Collections.sort(list, new Comparator<Entry<String, Integer>>()   
		{  
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)   
			{  
				if (order) {  
					//compare two object and return an integer  
					return o1.getValue().compareTo(o2.getValue());}   
				else {  
					return o2.getValue().compareTo(o1.getValue());  
				}  
			}  
		});  
		//prints the sorted HashMap  
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();  
		for (Entry<String, Integer> entry : list)   
		{  
			sortedMap.put(entry.getKey(), entry.getValue());  
		}  
		printMap(sortedMap);

		ArrayList<String> sortedTestByMaxKill = new ArrayList<String>();
		for(String test : sortedMap.keySet()) {
			sortedTestByMaxKill.add(test);
		}
		return sortedTestByMaxKill;
	}  

	//method for printing the elements  
	public void printMap(Map<String, Integer> map)   
	{    
		for (Entry<String, Integer> entry : map.entrySet())   
		{  
			System.out.println(entry.getKey() +"\t"+entry.getValue());  
		}  
		System.out.println("\n");  
	}

	public HashMap<Integer,String> getTestIDNameMapp() {
		return testIDNameMap;
	}

	public void setTestIDNameMap(HashMap<Integer,String> testIDNameMapp) {
		MutationKilledByTestParser.testIDNameMap = testIDNameMapp;
	}

	public int getZeroKillerCount() {
		return onlyKillerTestCount;
	}

	public void setZeroKillerCount(int zeroKillerCount) {
		this.onlyKillerTestCount = zeroKillerCount;
	}

	public String getMutationReportFile() {
		return mutationReportFile;
	}

	public void setMutationReportFile(String mutationReportFile) {
		this.mutationReportFile = mutationReportFile;
	}  
}
