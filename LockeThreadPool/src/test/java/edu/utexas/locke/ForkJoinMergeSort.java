package test.java.edu.utexas.locke;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("serial")
public class ForkJoinMergeSort extends RecursiveAction {

	private int[] array;
	private int[] workingArray;

	private int lo;
	private int hi;

	public ForkJoinMergeSort(int[] array) {
		this(array, new int[array.length], 0, array.length);
	}

	private ForkJoinMergeSort(int[] array, int[] workingArray, int lo, int hi) {
		this.array = array;
		this.workingArray = workingArray;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected void compute() {
		if (lo + 1 >= hi) {
			return;
		}

		int mid = (lo + hi) / 2;
		ForkJoinMergeSort lockeMergeSortLeft = new ForkJoinMergeSort(array, workingArray, lo, mid);
		lockeMergeSortLeft.fork();

		ForkJoinMergeSort lockeMergeSortRight = new ForkJoinMergeSort(array, workingArray, mid, hi);
		lockeMergeSortRight.compute();
		lockeMergeSortLeft.join();

		merge(lo, mid, hi);

		return;
	}

	private void merge(int lo, int mid, int hi) {
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

		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processors);
		System.out.println();

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

		ForkJoinMergeSort mergeSort = new ForkJoinMergeSort(actualSortedArray);
		ForkJoinPool pool = new ForkJoinPool(processors);
		pool.invoke(mergeSort);

		assert Arrays.equals(expectedSortedArray, actualSortedArray);
		System.out.println("Original Array: " + Arrays.toString(originalArray));
		System.out.println("Sorted Array: " + Arrays.toString(actualSortedArray));
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
