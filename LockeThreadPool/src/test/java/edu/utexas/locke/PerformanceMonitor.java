package test.java.edu.utexas.locke;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/*
 * Used to gather performance metrics in a semi-RAII way
 */
public class PerformanceMonitor {
	private long startTimeInNanoseconds;

	public PerformanceMonitor() {
		startTimeInNanoseconds = System.nanoTime();
	}

	public void gatherMetricsAndPrint() {
		double executionTimeInMilliseconds = (System.nanoTime() - startTimeInNanoseconds) / 10.0E6;
		System.out.println("Execution Time (ms): " + executionTimeInMilliseconds);
		System.out.println();

		long total = 0;
		try {
			String memoryUsage = new String();
			List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
			for (MemoryPoolMXBean pool : pools) {
				MemoryUsage peak = pool.getPeakUsage();
				long peakUsed = peak.getUsed();
				total += peakUsed;
				memoryUsage += String.format("Peak %s memory used: %,d%n", pool.getName(), peakUsed);
				memoryUsage += String.format("Peak %s memory reserved: %,d%n", pool.getName(), peak.getCommitted());
			}

			System.out.println(memoryUsage);
			System.out.println("Total peak memory used: " + total);

		} catch (Throwable t) {
			System.err.println("Exception in agent: " + t);
		}
	}
}
