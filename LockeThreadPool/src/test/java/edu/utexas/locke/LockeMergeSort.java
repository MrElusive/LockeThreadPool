package test.java.edu.utexas.locke;

import main.java.edu.utexas.locke.LockeTask;

import com.offbynull.coroutines.user.Continuation;


public class LockeMergeSort extends LockeTask<Void> {

	private int[] array;
	private int lo;
	private int hi;

	public LockeMergeSort(int[] array) {
		this(array, 0, array.length);
	}

	public LockeMergeSort(int[] array, int lo, int hi) {
		this.array = array;
		this.lo = 0;
		this.hi = array.length;
	}

	@Override
	protected Void compute(Continuation c) {
		if (lo + 1 >= hi) {
			return null;
		}

		int mid = (lo + hi) / 2;
		LockeMergeSort lockeMergeSortLeft = new LockeMergeSort(array, lo, mid);
		this.fork(c, lockeMergeSortLeft);

		LockeMergeSort lockeMergeSortRight = new LockeMergeSort(array, mid, hi);
		this.join(c, lockeMergeSortRight);

		merge(array, lo, mid, hi);

		return null;
	}

	private void merge(int[] array2, int lo2, int mid, int hi2) {
		// TODO Auto-generated method stub

	}

}
