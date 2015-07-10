package edu.utexas.locke;

// Clayton
public class Process extends Thread {	
	private ExecutionContext context;
	private LockeDeque deque;
	private LockeThread currentThread;
	
	public Process() {
		
	}
	
	// Executes the work-stealing algorithm
	public void run() {
		currentThread = deque.popBottom();
				
		while (ComputationTracker.isAllComputationDone()) {
			if (currentThread != null) { // Execute current thread
				LockeThreadPair pair = execute(currentThread);
								
				if (pair == null) { // The current thread blocked or terminated 
					currentThread = deque.popBottom();
								
				} else { // Another thread was enabled or spawned
					
					// @TODO: Every time we spawn a new child thread, we
					// need to call ComputationTacker.computationAdded()
					deque.pushBottom(pair.getFirstChild());
					currentThread = pair.getSecondChild();
				}				
				
			} else { // Steal
				yield();
				Process victim = LockeThreadPool.getRandomProcess();
				currentThread = victim.deque.popTop();
			}
		}
	}


	private LockeThreadPair execute(LockeThread currentThread) {
		
		// @TODO: need to save current thread state with a continuation
				
		currentThread.run(context);
		ComputationTracker.computationCompleted();
		return null;
	}




}
