package mkea.tcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import mkea.tcp.importTestReport.APMKCalculator;
import mkea.tcp.importTestReport.MutantInfo;

public class OptimalTCP {

	private ArrayList<String> testCases;
	private HashMap<MutantInfo, Set<String>> mutantMap;

	private APMKCalculator apmk = new APMKCalculator();
	private double apmkScore = 0.0;
	public static long count = 0;
	
	private ArrayList<String> lastBestOrder = new ArrayList<String>();
	
	public OptimalTCP(ArrayList<String> testCases, HashMap<MutantInfo, Set<String>> mutantMap) {
		this.testCases = testCases;
		this.mutantMap = mutantMap;
		lastBestOrder.addAll(testCases);
		apmkScore = apmk.APMK(this.mutantMap, lastBestOrder);
	}


	public void permute() {
		Permutation(0, testCases);
		System.out.println("APMK: "+apmkScore);
		System.out.println("Order: "+lastBestOrder);
	}

	private void Permutation(int i, ArrayList<String> nums) {
		if (i == nums.size() - 1) {
			ArrayList<String> list = new ArrayList<String>();
			for (String n : nums) list.add(n);
			double current = apmk.APMK(this.mutantMap, list);
			if(current > apmkScore) {
				count++;
				System.out.println("New: "+current+", Old: "+apmkScore+", Count:"+count);
				lastBestOrder.clear();
				lastBestOrder.addAll(list);
				apmkScore = current;
			}
//			
		} else {
			for (int j = i, l = nums.size(); j < l; j++) {
				String temp = nums.get(j);
				nums.set(j, nums.get(i));
				nums.set(i, temp);
				Permutation(i + 1, nums);
				temp = nums.get(j);
				nums.set(j, nums.get(i));
				nums.set(i, temp);
			}
		}
	}

}
