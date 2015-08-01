 package main.java.edu.utexas.locke;
import java.util.Collection;

// Sierra
public class LockeThreadPool {

	// @TODO: When the initial set of LockeThreads are added to
	// the LockeThreadPool, we need to call 
	// ComputationTracker.initialize(numLockeThreads);
	// This will allow us to keep track of when all LockeThreads
	// have completed.
	
	public LockeThreadPool(int numProcesses) {
		// TODO Auto-generated method stub
	}
	
	public void submit(Collection<LockeThread> threads) {
		// TODO Auto-generated method stub
	}

	
	public static LockeProcess getRandomProcess() {
		LockeProcess p = new LockeProcess();
		return p;
	}

}
