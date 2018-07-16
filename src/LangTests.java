import java.util.Arrays;

/*
 * A place for general tinkering to help get fluent in Java
 */
public class LangTests {
	
	public static void arraysTest(int len) {
		// initialise array
		int[] intArray = new int[len];
		// assign values
		for (int i=0; i<len; i++) {
			intArray[i] = i*i;
		}
		// print array as string
		System.out.println(Arrays.toString(intArray));
	}

	public static void main(String[] args) {
		arraysTest(3);
		arraysTest(5);
	}

}
