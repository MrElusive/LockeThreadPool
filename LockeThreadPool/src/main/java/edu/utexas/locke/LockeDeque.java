package main.java.edu.utexas.locke;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicStampedReference;

// Sierra
public class LockeDeque {

	private LinkedList<LockeThread> deque;
	private AtomicStampedReference<Integer> top;
	private int bot;

	public LockeDeque() {
		deque = new LinkedList<LockeThread>();
		top = new AtomicStampedReference<Integer>(0, 0);
		bot = 0;
	}

	public LockeThread popBottom() {
		int localBot = bot;
		if (localBot == 0) {
			return null;
		}
		bot = --localBot;
		LockeThread thread = deque.get(localBot);

		int[] topAge = {0};
		int localTop = top.get(topAge);
		if (localBot > localTop) {
			return thread;
		}

		bot = 0;
		int newTopAge = topAge[0] + 1;
		int newTop = 0;
		if (localBot == localTop) {
			if (top.compareAndSet(localTop, newTop, topAge[0], newTopAge)) {
				return thread;
			}
		}
		top.set(newTop, newTopAge);
		return null;
	}

	// Process from thread bottom
	public void pushBottom(final LockeThread thread) {
		int localBot = bot;
		if (localBot > deque.size()) {
			deque.add(thread);
		} else {
			deque.add(localBot, thread);
		}
		bot = localBot + 1;
	}

	//Steal from the top
	public LockeThread popTop() {
		int[] topAge = {0};
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
}

/*
import java.util.concurrent.ConcurrentLinkedDeque;

// Sierra
public class LockeDeque {

	ConcurrentLinkedDeque<LockeThread> deque;

	public LockeDeque() {
		deque = new ConcurrentLinkedDeque<LockeThread>();
	}

	public LockeThread popBottom() {
		return deque.pollLast();
	}

	public void pushBottom(LockeThread thread) {
		deque.addLast(thread);
	}

	public LockeThread popTop() {
		return deque.pollFirst();
	}
}
*/
