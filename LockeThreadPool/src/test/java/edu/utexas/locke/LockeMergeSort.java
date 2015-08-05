package test.java.edu.utexas.locke;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntUnaryOperator;

import main.java.edu.utexas.locke.LockeTask;
import main.java.edu.utexas.locke.LockeThreadPool;

public class LockeMergeSort extends LockeTask<Void> {

	private int[] array;
	private int[] workingArray;

	private int lo;
	private int hi;

	public LockeMergeSort(int[] array) {
		this(array, new int[array.length], 0, array.length);
	}

	private LockeMergeSort(int[] array, int[] workingArray, int lo, int hi) {
		this.array = array;
		this.workingArray = workingArray;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected Void compute() {
		if (lo + 1 >= hi) {
			return null;
		}

		int mid = (lo + hi) / 2;
		LockeMergeSort lockeMergeSortLeft = new LockeMergeSort(array, workingArray, lo, mid);
		lockeMergeSortLeft.fork();

		LockeMergeSort lockeMergeSortRight = new LockeMergeSort(array, workingArray, mid, hi);
		lockeMergeSortRight.compute();
		lockeMergeSortLeft.join();

		merge(lo, mid, hi);
		return null;
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

		LockeMergeSort mergeSort = new LockeMergeSort(actualSortedArray);
		LockeThreadPool pool = new LockeThreadPool(processors);
		pool.invoke(mergeSort);

		assert Arrays.equals(expectedSortedArray, actualSortedArray);
		System.out.println("Original Array: " + Arrays.toString(originalArray));
		System.out.println("Sorted Array: " + Arrays.toString(actualSortedArray));
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
