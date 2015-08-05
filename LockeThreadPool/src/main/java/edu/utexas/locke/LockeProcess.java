package main.java.edu.utexas.locke;

import java.util.concurrent.Semaphore;

/*
 * A LockeProcess is executed as a kernel-mode thread and is scheduled by the OS.
 * The LockeProcess in turn executes LockeThreads using a work-stealing algorithm
 * similar to the one described here: http://www.geocities.ws/nimar_arora/publications/tocs01.pdf.
 */
public class LockeProcess extends Thread {

	private static ThreadLocal<LockeProcess> currentProcess = new ThreadLocal<LockeProcess>();

	private LockeThreadPool lockeThreadPool;
	private Semaphore startSemaphore;
	private LockeDeque deque;


	public LockeProcess(LockeThreadPool lockeThreadPool, Semaphore startSemaphore) {
		this.lockeThreadPool = lockeThreadPool;
		this.startSemaphore = startSemaphore;
		this.deque = new LockeDeque();
	}

	public static LockeProcess currentProcess() {
		return currentProcess.get();
	}

	public LockeDeque getDeque() {
		return deque;
	}

	@Override
	public void run() {
		currentProcess.set(this);
		try {
			startSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (!ComputationTracker.done()) {
			workSteal();
		}
	}

	// Executes the work-stealing algorithm.
	// If there is a thread in the deque, execute it.
	// Otherwise, attempt to steal from another thread.
	public void workSteal() {
		LockeThread thread = deque.popBottom();
		if (thread == null) {
			thread = stealThread();
		}

		if (thread != null) {
			thread.exec();
		}
	}

	// Attempts to steal a LockeThread from another LockeProcess.
	private LockeThread stealThread() {
		yield();

		LockeProcess victim = this.lockeThreadPool.getRandomProcess();
		while (victim == this) {
			victim = this.lockeThreadPool.getRandomProcess();
		}
		assert(victim != null);

		return victim.deque.popTop();
	}
}
