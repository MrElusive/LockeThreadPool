package test.java.edu.utexas.locke;

public class RecursiveFibonacci {

	public static long compute(int n) {
		if ((n == 0) || (n == 1))
			return 1L;
		return compute(n - 1) + compute(n - 2);
	}

	public static void main(String[] args) {
		PerformanceMonitor perfMon = new PerformanceMonitor();

		int n = 25;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}
		long result = compute(n);
		System.out.println("Result: " + result);
		System.out.println();

		perfMon.gatherMetricsAndPrint();
	}
}
