package test.java.edu.utexas.locke;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntUnaryOperator;

public class RecursiveMergeSort {

	public static void compute(int[] array) {
		compute(array, new int[array.length], 0, array.length);
	}

	private static void compute(int[] array, int[] workingArray, int lo, int hi) {
		if (lo + 1 >= hi) {
			return;
		}

		int mid = (lo + hi) / 2;
		compute(array, workingArray, lo, mid);
		compute(array, workingArray, mid, hi);

		merge(array, workingArray, lo, mid, hi);
	}

	private static void merge(int[] array, int[] workingArray, int lo, int mid, int hi) {
		int left = lo;
		int right = mid;

		for (int j = lo; j < hi; j++) {
			if (left < mid && (right >= hi || array[left] <= array[right])) {
				workingArray[j] = array[left++];
			} else {
				workingArray[j] = array[right++];
			}
		}

		for (int j = lo; j < hi; j++) {
			array[j] = workingArray[j];
		}
	}

	public static void main(String[] args) {
		PerformanceMonitor perfMon = new PerformanceMonitor();

		int originalArrayLength = 100;
		if (args.length > 0) {
			originalArrayLength = Integer.parseInt(args[0]);
		}

		int[] originalArray = new int[originalArrayLength];
		Arrays.setAll(originalArray, new IntUnaryOperator() {
			private Random random = new Random();

			@Override
			public int applyAsInt(int operand) {
				return random.nextInt(100);
			}
		});
		int[] expectedSortedArray = originalArray.clone();
		int[] actualSortedArray = originalArray.clone();

		Arrays.sort(expectedSortedArray);

		compute(actualSortedArray);

		assert Arrays.equals(expectedSortedArray, actualSortedArray);
		System.out.println("Original Array: " + Arrays.toString(originalArray));
		System.out.println("Sorted Array: " + Arrays.toString(actualSortedArray));
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
