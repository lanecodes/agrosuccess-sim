package me.ajlane.random;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ArrayUtilsTest {
	
	int[] deckArray;
	
	static void printIntArray1D(String desc, int[] array) {
		System.out.print(desc + ": ");
		for (int i=0; i<array.length; i++) {
			System.out.print(array[i] + " ");
		}	
		System.out.println();
	}
	
	/**
	 * Set up an array representing our 'deck' of 5 cells which we'll shuffle
	 * and draw from.
	 */
	@Before
	public void makedeckArray() {
		deckArray = IntStream.range(0, 5).toArray();
		//printIntArray1D("Initial deck array", deckArray);
	}	
	
	/**
	 * Test ArrayUtils.randomDrawFromArray to ensure that each possible combination
	 * is equally likely.
	 * 
	 * As a test case we use 5-choose-2 which has 10 possible combinations:
	 * 0 1
	 * 0 2
	 * 0 3
	 * 0 4
	 * 1 2
	 * 1 3
	 * 1 4
	 * 2 3
	 * 2 4
	 * 3 4
	 * 
	 * As a test, we draw 5000000 pairs and check that each combination had a probability of 
	 * 0.1 of being selected. 5000000 selected as a reasonable trade off between speed and 
	 * empirically likelihood of frequencies having converged sufficiently for test to
	 * reliably pass
	 */
	@Ignore // this passes, but convergence takes to long to run as part of test suite
	@Test 
	public void combinatoricSubsetsShouldBeEquallyLikely() {
		
		HashMap<	ArrayList<Integer>, Integer> comboCounts = new HashMap<ArrayList<Integer>, Integer>();
		comboCounts.put(getArrayList(new int[] {0, 1}), 0);
		comboCounts.put(getArrayList(new int[] {0, 2}), 0);
		comboCounts.put(getArrayList(new int[] {0, 3}), 0);
		comboCounts.put(getArrayList(new int[] {0, 4}), 0);
		comboCounts.put(getArrayList(new int[] {1, 2}), 0);
		comboCounts.put(getArrayList(new int[] {1, 3}), 0);
		comboCounts.put(getArrayList(new int[] {1, 4}), 0);
		comboCounts.put(getArrayList(new int[] {2, 3}), 0);
		comboCounts.put(getArrayList(new int[] {2, 4}), 0);
		comboCounts.put(getArrayList(new int[] {3, 4}), 0);	
		
		ArrayList<Integer> hand;		
		int testCounter = 0;
		int totalTests = 5000000;
		
		// count number of times each possible combination comes up
		while (testCounter < totalTests) {
			hand = getArrayList(ArrayUtils.randomDrawFromArray(2, deckArray, true));
			comboCounts.put(hand, comboCounts.get(hand)+1);
			testCounter++;
		}		
		
		// convert counts to frequencies, check all within tolerance of 0.1
		for (ArrayList<Integer> combo : comboCounts.keySet()) {
			double comboProb = (double)comboCounts.get(combo)/totalTests;
			System.out.println(combo + " frequency: " + comboProb);
			assertEquals(0.1, comboProb, 0.01);									
		}		
	}	
	
	/**
	 * Helper function to return an ArrayList from an array
	 * @param ar
	 * @return
	 */
	private ArrayList<Integer> getArrayList(int[] ar) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i=0; i<ar.length; i++) {
			Integer intObject = Integer.valueOf(ar[i]);
			al.add(intObject);
		}
		return al;
	}

}
