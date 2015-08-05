package main.java.edu.utexas.locke;

/*
 * Represents a chunk of work that is necessary to compute a desired value.
 * Clients must override compute().
 */
public abstract class LockeTask<T> extends LockeThread {
	private T value;

	// Overridden by clients to perform their computation.
	protected abstract T compute();
	
	@Override
	protected void run() {
		value = compute();
	}

	// Wait until this LockeTask is completed before retrieving its computed value.
	public T joinGet() {
		super.join();
		return value;
	}

	// Immediately retrieve this task's value.
	// This should be used in conjunction with LockeThread.waitDone(),
	// as shown in the LockeThreadPool.invoke().
	public T get() {
		return value;
	}
}
