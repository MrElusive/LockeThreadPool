package test.java.edu.utexas.locke;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class PerformanceMonitor {
	private long startTimeInNanoseconds;

	public PerformanceMonitor() {
		startTimeInNanoseconds = System.nanoTime();
	}

	public void gatherMetricsAndPrint() {
		double executionTimeInMilliseconds = (System.nanoTime() - startTimeInNanoseconds) / 1000.0;
		System.out.println("Execution Time (ms): "
				+ executionTimeInMilliseconds);
		long total = 0;
		try {
			String memoryUsage = new String();
			List<MemoryPoolMXBean> pools = ManagementFactory
					.getMemoryPoolMXBeans();
			for (MemoryPoolMXBean pool : pools) {
				MemoryUsage peak = pool.getPeakUsage();
				long peakUsed = peak.getUsed();
				total += peakUsed;
				memoryUsage += String.format("Peak %s memory used: %,d%n",
						pool.getName(), peakUsed);
				memoryUsage += String.format("Peak %s memory reserved: %,d%n",
						pool.getName(), peak.getCommitted());
			}

			// we print the result in the console
			System.out.println(memoryUsage);
			System.out.println("Total peak memory used: " + total);

		} catch (Throwable t) {
			System.err.println("Exception in agent: " + t);
		}
	}
}
