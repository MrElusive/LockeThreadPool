package test.java.edu.utexas.locke;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
public class ForkJoinFibonacci extends RecursiveTask<Integer> {
	final int n;

	ForkJoinFibonacci(int n) {
		this.n = n;
	}

	@Override
	protected Integer compute() {
		if ((n == 0) || (n == 1)) {
			return 1;
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
		ForkJoinFibonacci f = new ForkJoinFibonacci(Integer.parseInt(args[0]));
		ForkJoinPool pool = new ForkJoinPool(processors);
		int result = pool.invoke(f);
		System.out.println("Result: " + result);

		perfMon.gatherMetricsAndPrint();
	}
}
