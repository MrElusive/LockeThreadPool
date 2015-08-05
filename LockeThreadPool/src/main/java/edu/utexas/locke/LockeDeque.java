package main.java.edu.utexas.locke;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicStampedReference;

/*
 * A specialized concurrent double-ended queue that is optimized for LockeProcesses.
 * Note that each LockeProcess in the pool will own their own LockeDeque. Because
 * each LockeProcess will be the only one to push or pop from the bottom of its own LockeDeque,
 * the pushButtom() method requires no synchronization. The popBottom() method only requires
 * synchronization with respect to the popTop() method.
 * 
 * See http://www.geocities.ws/nimar_arora/publications/tocs01.pdf for more details
 */
public class LockeDeque {
	private static final int DEQUE_SIZE = 100;

	private ArrayList<LockeThread> deque;
	private AtomicStampedReference<Integer> top;
	private int bot;

	public LockeDeque() {
		deque = new ArrayList<LockeThread>(DEQUE_SIZE);
		top = new AtomicStampedReference<Integer>(0, 0);
		bot = 0;
	}

	// The owning LockeProcess of this deque will pop LockeThreads from off the
	// bottom.
	public void pushBottom(final LockeThread thread) {
		int localBot = bot;
		if (localBot >= deque.size()) {
			deque.add(thread);
		} else {
			deque.set(localBot, thread);
		}
		localBot = localBot + 1;
		bot = localBot;
	}

	// The owning LockeProcess of this deque will push LockeThreads onto the
	// bottom.
	public LockeThread popBottom() {
		int localBot = bot;
		if (localBot == 0) {
			return null;
		}
		localBot = localBot - 1;
		bot = localBot;
		LockeThread thread = deque.get(localBot);
		int[] topAge = { 0 };
		int localTop = top.get(topAge);
		if (localBot > localTop) {
			return thread;
		}
		bot = 0;
		int newTop = 0;
		int newTopAge = topAge[0] + 1;
		if (localBot == localTop) {
			if (top.compareAndSet(localTop, newTop, topAge[0], newTopAge)) {
				return thread;
			}
		}
		top.set(newTop, newTopAge);
		return null;
	}

	// Other LockeProcesses will attempt to steal from the top of this deque.
	public LockeThread popTop() {
		int[] topAge = { 0 };
		int localTop = top.get(topAge);
		int localBot = bot;
		if (localBot <= localTop) {
			return null;
		}
		LockeThread thread = deque.get(localTop);
		if (top.compareAndSet(localTop, localTop + 1, topAge[0], topAge[0])) {
			return thread;
		}
		return null;
	}

	public int size() {
		return bot - top.getReference();
	}
}