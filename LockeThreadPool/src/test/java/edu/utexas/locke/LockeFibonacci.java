package test.java.edu.utexas.locke;

import main.java.edu.utexas.locke.LockeTask;
import main.java.edu.utexas.locke.LockeThreadPool;

class LockeFibonacci extends LockeTask<Long> {
	final long n;

	LockeFibonacci(long n) {
		this.n = n;
	}

	@Override
	protected Long compute() {
		if ((n == 0) || (n == 1)) {
			return 1L;
		}
		LockeFibonacci f1 = new LockeFibonacci(n - 1);
		f1.fork();
		LockeFibonacci f2 = new LockeFibonacci(n - 2);
		return f2.compute() + f1.joinGet();
	}

	public static void main(String[] args) {
		PerformanceMonitor perfMon = new PerformanceMonitor();

		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processors);
		System.out.println();

		int n = 25;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}
		LockeFibonacci f = new LockeFibonacci(n);

		LockeThreadPool pool = new LockeThreadPool(processors);
		long result = pool.invoke(f);
		System.out.println("Result: " + result);
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
