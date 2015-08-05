package main.java.edu.utexas.locke;

import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.edu.utexas.locke.SynchronizationOperation.Type;

import com.offbynull.coroutines.user.Continuation;
import com.offbynull.coroutines.user.Coroutine;
import com.offbynull.coroutines.user.CoroutineRunner;

// Vince
public abstract class LockeThread implements Coroutine {

	private static ThreadLocal<LockeThread> currentThread = new ThreadLocal<LockeThread>();

	private CoroutineRunner runner;

	public volatile boolean isDone;

	public LockeThread parentThread;
	public ConcurrentLinkedQueue<LockeThread> children;

	public LockeThread() {
		this.runner = new CoroutineRunner(this);
		this.isDone = false;
		this.parentThread = currentThread();
		this.children = new ConcurrentLinkedQueue<LockeThread>();
	}

	public void fork(Continuation continuation, LockeThread threadToFork) {
		//assert this == currentThread();
		assert threadToFork != null;
		this.children.add(threadToFork);

		SynchronizationOperation forkOperation = new SynchronizationOperation(Type.FORK);
		forkOperation.addReadyThread(threadToFork);

		continuation.setContext(forkOperation);
		continuation.suspend();
	}

	public void join(Continuation continuation, LockeThread threadToJoin) {
		//assert this == currentThread();
		assert threadToJoin != null;
		assert this.children.contains(threadToJoin);

		SynchronizationOperation joinOperation = new SynchronizationOperation(Type.JOIN);
		joinOperation.addReadyThread(threadToJoin);

		continuation.setContext(joinOperation);
		continuation.suspend();
	}

	public static LockeThread currentThread() {
		return currentThread.get();
	}

	// package-private because Process needs to call this, but users should not
	SynchronizationOperation execute() {
		currentThread.set(this);
		boolean finished = !runner.execute();
		currentThread.set(null);

		if (finished) {
			this.isDone = true;
			SynchronizationOperation terminateOperation = new SynchronizationOperation(Type.TERMINATE);
			return terminateOperation;
		} else {
			return (SynchronizationOperation) runner.getContext();
		}
	}

	public synchronized void notifyDone() {
		notify();
	}

	public synchronized void waitDone() throws InterruptedException {
		while (!isDone) {
			wait();
		}
	}
}