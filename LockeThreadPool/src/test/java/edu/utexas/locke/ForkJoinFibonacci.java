package test.java.edu.utexas.locke;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
public class ForkJoinFibonacci extends RecursiveTask<Long> {
	final long n;

	ForkJoinFibonacci(long n) {
		this.n = n;
	}

	@Override
	protected Long compute() {
		if ((n == 0) || (n == 1)) {
			return 1L;
		}

		ForkJoinFibonacci f1 = new ForkJoinFibonacci(n - 1);
		f1.fork();
		ForkJoinFibonacci f2 = new ForkJoinFibonacci(n - 2);
		return f2.compute() + f1.join();
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

		ForkJoinFibonacci f = new ForkJoinFibonacci(n);
		ForkJoinPool pool = new ForkJoinPool(processors);
		long result = pool.invoke(f);
		System.out.println("Result: " + result);
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
