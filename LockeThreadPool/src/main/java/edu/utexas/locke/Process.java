package main.java.edu.utexas.locke;

// Clayton
public class Process extends Thread {	
	private LockeDeque deque;
	private LockeThread currentThread;
	
	public Process() {
		
	}
	
	// Executes the work-stealing algorithm
	public void run() {
		currentThread = deque.popBottom();
				
		while (ComputationTracker.done()) {
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
		assert (victim != null);
		
		return victim.deque.popTop();
	}

	private LockeThread execute(LockeThread thread) {
		assert (thread != null);
		
		SynchronizationOperation synchronizationOperation = thread.execute();
		assert (synchronizationOperation != null);
		
		LockeThread readyThread = synchronizationOperation.getReadyThread();
		
		switch (synchronizationOperation.getType()) {
			case FORK:
				assert (readyThread != null);
				ComputationTracker.increment();
				deque.pushBottom(readyThread);
				return thread;
			case TERMINATE:
				ComputationTracker.decrement();
				if (readyThread != null) return readyThread;
				return deque.popBottom();
			case BLOCK:
				assert (readyThread == null);
				return deque.popBottom();
			case JOIN:
				assert (readyThread == null);
				return deque.popBottom();
			default:
				// @TODO: exception?
				return null;
			
		}
	}
}
