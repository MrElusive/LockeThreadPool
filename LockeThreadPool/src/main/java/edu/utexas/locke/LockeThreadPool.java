 package main.java.edu.utexas.locke;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Sierra
public class LockeThreadPool {

	// @TODO: When the initial set of LockeThreads are added to
	// the LockeThreadPool, we need to call
	// ComputationTracker.initialize(numLockeThreads);
	// This will allow us to keep track of when all LockeThreads
	// have completed.

	Set<LockeProcess> processes;

	public LockeThreadPool(int numProcesses) {
		processes = new HashSet<LockeProcess>();
		for (int i = 0; i < numProcesses; i++) {
			LockeProcess process = new LockeProcess(startSemaphore);
			process.start();
			processes.add(process);
		}
	}

	public void submit(Collection<LockeThread> threads) {
		for (LockeThread thread : threads) {
			getRandomProcess().submit(thread);
		}

		startSemaphore.release(processes.size());
	}

	public <T> T invoke(LockeTask<T> task) {
		return getRandomProcess().invokeTask(task);
	}

	public static LockeProcess getRandomProcess() {
		return null;
	}
}
