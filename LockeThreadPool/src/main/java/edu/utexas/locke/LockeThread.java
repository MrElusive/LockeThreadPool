package main.java.edu.utexas.locke;

/*
 * Represents a thread of execution in a process.
 * Each LockeProcess will execute one LockeThread at a time.
 * 
 * If a LockeThread is forked, that new LockeThread is added
 * to the deque of the currently executing process. See fork() below.
 * 
 * If a LockeThread is joined on, the LockeThread that is joining must wait
 * until the LockeThread that is being joined on has finished. In the meantime,
 * the LockeProcess of the LockeThread that is joining will try to execute other
 * threads. See join() below.
 */
public abstract class LockeThread {

	private volatile boolean isDone;
	
	public void exec() {
		run();
		finish();
		ComputationTracker.decrement();
	}

	protected abstract void run();
	
	private synchronized void finish() {
		isDone = true;
		notifyAll(); // Notify any Java threads that might be waiting for this LockeThread to complete
	}
	
	public void fork() {
		ComputationTracker.increment();
		LockeProcess.currentProcess().getDeque().pushBottom(this);
	}
	
	public void join() {
		while (!isDone) {
			LockeProcess.currentProcess().workSteal();
		}
	}

	// Although similar to join() above, this method causes the entire Java thread (LockeProcess)
	// to pause execution until the isDone condition is true.
	public synchronized void waitDone() throws InterruptedException {
		while (!isDone) {
			wait();
		}
	}
}