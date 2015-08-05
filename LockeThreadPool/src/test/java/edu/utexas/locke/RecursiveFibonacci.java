package test.java.edu.utexas.locke;


public class RecursiveFibonacci {

	public static int compute(int n) {
		if ((n == 0) || (n == 1))
			return 1;
		return compute(n - 1) + compute(n - 2);
	}

	public static void main(String[] args) {
		PerformanceMonitor perfMon = new PerformanceMonitor();

		int n = Integer.parseInt(args[0]);
		int result = compute(n);
		System.out.println("Result: " + result);

		perfMon.gatherMetricsAndPrint();
	}
}
