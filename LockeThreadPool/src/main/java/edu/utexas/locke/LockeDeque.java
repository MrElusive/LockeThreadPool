package main.java.edu.utexas.locke;


import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Sierra
public abstract class LockeDeque<E> implements Deque<E> {
	
	private LinkedList<E> deque = new LinkedList<E>();	
	private ReentrantLock topReentratLock = new ReentrantLock();
	private ReentrantLock bottomReentratLock = new ReentrantLock();
	private Object mutex = new Object();

	public LockeThread popBottom(LockeThread thread) {
		while (!isEmpty() && bottomReentratLock.tryLock()) {
			bottomReentratLock.lock();
			blockOtherEnd(topReentratLock);
			try {
				this.deque.removeLast();
				outputExecution("Pop Bottom", thread);
			} finally {
				this.bottomReentratLock.unlock();
				unblockOtherEnd(topReentratLock);
			}
		}	
		return thread;
	}

	public LockeThread pushBottom(E item, LockeThread thread) {
		synchronized (mutex) {
			this.deque.addLast(item);
			outputExecution("Push bottom", thread);
		}
		return thread;
	}

	public LockeThread popTop(LockeThread thread) throws InterruptedException {
		while (!isEmpty() && topReentratLock.tryLock()) {
			topReentratLock.lock();
			blockOtherEnd(bottomReentratLock);
			try {
				this.deque.removeFirst();
				outputExecution("Pop Top", thread);
			} finally {
				this.topReentratLock.unlock();
				unblockOtherEnd(bottomReentratLock);
			}
		}
		return thread;
	}
	
	public boolean isEmpty() {
		return deque.size() == 0;
	}
	
	private void outputExecution(String operation, LockeThread thread) {
		System.out.println("Thread ID: " + thread + " is doing " + operation);
		System.out.println("Thread ID: " + thread + " is doing " + "deque Current Size: " + deque.size());
	}

	private void blockOtherEnd(Lock lock) {
		lock.lock();
	}

	private void unblockOtherEnd(Lock lock) {
		lock.unlock();
	}

}

