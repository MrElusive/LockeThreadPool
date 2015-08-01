package test.java.edu.utexas.locke;

import main.java.edu.utexas.locke.LockeTask;
import main.java.edu.utexas.locke.LockeThreadPool;

class LockeFibonacci extends LockeTask<Integer> {
	final int n;

	LockeFibonacci(int n) {
		this.n = n;
	}

	@Override
	protected Integer compute() {
		if ((n == 0) || (n == 1)) {
			return 1;
		}
		LockeFibonacci f1 = new LockeFibonacci(n - 1);
		f1.fork();
		LockeFibonacci f2 = new LockeFibonacci(n - 2);
		return f2.compute() + f1.get();
	}

	public static void main(String[] args) {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processors);

		LockeFibonacci f = new LockeFibonacci(Integer.parseInt(args[0]));

		LockeThreadPool pool = new LockeThreadPool(processors);
		int result = pool.invoke(f);
		System.out.println("Result: " + result);
	}
}
