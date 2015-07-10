package main.java.edu.utexas.locke;
import java.util.concurrent.atomic.AtomicLong;

public class ComputationTracker {
	
	private static AtomicLong computationsRemaining;

	public static boolean isAllComputationDone() {
		return computationsRemaining.get() == 0;
	}
	
	public static void computationAdded() {
		computationsRemaining.incrementAndGet();
		
	}

	public static void computationCompleted() {
		computationsRemaining.decrementAndGet();		
	}

}
