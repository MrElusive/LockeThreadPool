package main.java.edu.utexas.locke;

import com.offbynull.coroutines.user.Continuation;

public abstract class LockeTask<T> extends LockeThread {
	private T value;

	protected abstract T compute(Continuation c);

	// Clients should not override this method!
	@Override
	public void run(Continuation c) {
		value = compute(c);
	}

	public T waitGet(Continuation c) {
		LockeThread currentThread = LockeThread.currentThread();
        currentThread.join(c, this);
        return value;
	}

	public T get() {
		return value;
	}

}
