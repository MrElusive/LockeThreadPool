package main.java.edu.utexas.locke;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

// Clayton
public class LockeProcess extends Thread {
	private static ThreadLocal<LockeProcess> currentProcess = new ThreadLocal<LockeProcess>();
	private static ConcurrentHashMap<LockeThread, LockeThread> joinMap = new ConcurrentHashMap<LockeThread, LockeThread>();

	private LockeDeque deque;
	private Semaphore startSemaphore;

	public LockeDeque getDeque() {
		return deque;
	}

	public LockeProcess(Semaphore startSemaphore) {
		this.startSemaphore = startSemaphore;
		deque = new LockeDeque();
	}

	public static LockeProcess currentProcess() {
		return currentProcess.get();
	}

	// Executes the work-stealing algorithm
	@Override
	public void run() {
		currentProcess.set(this);
		try {
			startSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LockeThread currentThread = deque.popBottom();

		while (!ComputationTracker.done()) {
			if (currentThread != null) {
				currentThread = executeThread(currentThread);
			} else {
				currentThread = stealThread();
			}
		}
	}

	// Executes a thread and returns another thread to execute if there is one
	private LockeThread executeThread(LockeThread thread) {
		assert (thread != null);

		SynchronizationOperation synchronizationOperation = thread.execute();
		assert (synchronizationOperation != null);

		SynchronizationOperation.Type type = synchronizationOperation.getType();
		assert (type != null);

		List<LockeThread> readyThreads = synchronizationOperation
				.getReadyThreads();
		assert (readyThreads != null);

		LockeThread nextThread = null;

		switch (type) {
		case FORK:
			assert (readyThreads.size() == 1);
			ComputationTracker.increment();
			deque.pushBottom(readyThreads.get(0));
			nextThread = thread;
			break;
		case TERMINATE:
			ComputationTracker.decrement();
			// A client may have called invoke to execute this thread
			// Let the client know that the result is ready
			thread.notifyDone();

			LockeThread parentThread = thread.parentThread;
			if (parentThread != null) {
				synchronized (joinMap) {
					if (joinMap.containsKey(thread)) {
						joinMap.remove(thread);
						nextThread = parentThread;
					} else {
						joinMap.put(thread, parentThread);
						nextThread = deque.popBottom();
					}
				}
			} else {
				nextThread = deque.popBottom();
			}
			break;

		// There may have been multiple threads waiting to join on the
		// one that just terminated.
		// for (LockeThread readyThread : readyThreads) {
		// deque.pushBottom(readyThread);
		// }

		case JOIN:
			assert (readyThreads.size() == 1);
			LockeThread childThread = readyThreads.get(0);
			synchronized (joinMap) {
				if (joinMap.containsKey(childThread)) {
					joinMap.remove(childThread);
					nextThread = thread;
				} else {
					joinMap.put(childThread, thread);
					nextThread = deque.popBottom();
				}
			}
			break;
		default:
			throw new RuntimeException(String.format(
					"Error: Unrecognized synchronization operation (%s)",
					type.name()));
		}

		return nextThread;
	}

	// attempts to steal a thread from another process
	private LockeThread stealThread() {
		yield();
		LockeProcess victim = LockeThreadPool.getRandomProcess();
		assert (victim != null);

		return victim.deque.popTop();
	}

	// adds a single task to the work queue and waits for it to be completed
	public <T> T invokeTask(LockeTask<T> lockeTask) {
		deque.pushBottom(lockeTask);

		// Wait for the condition that this task has terminated and the
		// result is ready
		try {
			lockeTask.waitDone();
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"Error: LockeTask invocation caused an unexpected InterruptedException.");
		}

		return lockeTask.get();
	}

	public void submit(LockeThread thread) {
		deque.pushBottom(thread);
	}
}
