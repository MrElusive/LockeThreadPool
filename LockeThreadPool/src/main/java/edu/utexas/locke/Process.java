package main.java.edu.utexas.locke;

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
			if (currentThread != null) {
				currentThread = execute(currentThread);
			} else {
				currentThread = steal();
			}
		}
	}

	private LockeThread steal() {
		yield();
		Process victim = LockeThreadPool.getRandomProcess();
		return victim.deque.popTop();
	}

	private LockeThread execute(LockeThread thread) {
		SynchronizationOperation synchronizationOperation = thread.execute();
		
		if (synchronizationOperation == null) { // The current thread terminated
			return deque.popBottom();
					
		} else { // Handle the synchronization operation
			switch (synchronizationOperation.getType()) {
				case BLOCK:
					// store the thread somewhere until it is unblocked.
					return null;
				case FORK:
					ForkOperation operation = (ForkOperation) synchronizationOperation;
					deque.pushBottom(operation.getChildThread());
					return thread;
				case JOIN:
					// Is this necessary?
					return null;
				default:
					// @TODO: exception?
					return null;
				
			}
		}
	}
}
