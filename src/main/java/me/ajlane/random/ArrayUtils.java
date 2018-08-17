package me.ajlane.random;

import cern.jet.random.Uniform;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * See this https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array SO for 
 * inspiration
 * 
 * See here for filling array initially https://stackoverflow.com/questions/16020741/shortest-way-of-filling-an-array-with-1-2-n
 *  * @author andrew
 *
 */
public class ArrayUtils {
	
	public static void main(String args[]) {
		int[] solutionArray = { 1, 2, 3, 4, 5, 6, 16, 15, 14, 13, 12, 11 };
		
		for (int i=0; i<6; i++) {
			shuffleArray(solutionArray);
			int[] subset = Arrays.copyOfRange(solutionArray, 0, 3);
			
		    for (int j = 0; j < subset.length; j++) {
		    	System.out.print(subset[j] + " ");
		    }
		    System.out.println();			
		}
	}
	
	// Implementing Fisher–Yates shuffle
	static void shuffleArray(int[] ar) {
		Uniform rnd = new Uniform(0, 1, (int)System.currentTimeMillis()); 
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextIntFromTo(0, i);
			// Simple swap
		    int a = ar[index];
		    ar[index] = ar[i];
		    ar[i] = a;
		}
	}
	
	/**
	 * Utility method to map a List<Integer> to a int[]. Non-trivial apparently.
	 * 
	 * @param list
	 * 			Object to convert to an int[]
	 * @return
	 * 			Equivalent int[]
	 */
	private static int[] listToIntArray(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int index=0; index<list.size(); index++) {
			array[index] = (int)list.get(index);
		}
		return array;
	}
	
	/**
	 * @param n
	 * 			Number of elements to draw at random from array
	 * @param array
	 * 			Array to shuffle and draw from. Note that array will
	 * 			be reordered by this operation.
	 * @return
	 * 			n elements from array, drawn at random with equal 
	 * 			probability
	 */
	public static int[] randomDrawFromArray(int n, int[] array) {
		shuffleArray(array);
		return Arrays.copyOfRange(array, 0, n);		
	}
	
	
	/**
	 * @param n
	 * 			Number of elements to draw at random from list
	 * @param list
	 * 			list to shuffle and draw from. 
	 * @return
	 * 			n elements from list, drawn at random with equal 
	 * 			probability, returned as a list
	 */
	public static List<Integer> randomDrawFromList(int n, List<Integer> list) {
		int[] shuffledArray = randomDrawFromArray(n, listToIntArray(list));
		return Arrays.stream(shuffledArray).boxed().collect(Collectors.toList());		
	}
	
	/**
	 * @param n
	 * 			Number of elements to draw at random from array
	 * @param array
	 * 			Array to shuffle and draw from. Note that array will
	 * 			be reordered by this operation.
	 * @param sorted
	 * 			Whether or not to sort resulting array
	 * @return
	 * 			n elements from array, drawn at random with equal 
	 * 			probability
	 */
	public static int[] randomDrawFromArray(int n, int[] array, boolean sorted) {
		int[] hand = randomDrawFromArray(n, array);
		if (sorted==true) {
			Arrays.sort(hand);
		}
		return hand;				
	}	
}