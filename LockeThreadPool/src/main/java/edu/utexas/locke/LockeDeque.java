package main.java.edu.utexas.locke;


import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Sierra
public class LockeDeque {
	
	//private LinkedList<E> deque = new LinkedList<E>();	
	private ReentrantLock topReentratLock = new ReentrantLock();
	private ReentrantLock bottomReentratLock = new ReentrantLock();
	private Object mutex = new Object();

	public LockeThread popBottom() {
		while (!isEmpty() && bottomReentratLock.tryLock()) {
			bottomReentratLock.lock();
			blockOtherEnd(topReentratLock);
			try {
				//this.deque.removeLast();
			} finally {
				this.bottomReentratLock.unlock();
				unblockOtherEnd(topReentratLock);
			}
		}	
		return null;
	}

	public void pushBottom(LockeThread thread) {
		synchronized (mutex) {
			outputExecution("Push bottom", thread);
		}
	}

	public LockeThread popTop() {
		while (!isEmpty() && topReentratLock.tryLock()) {
			topReentratLock.lock();
			blockOtherEnd(bottomReentratLock);
			try {
				//this.deque.removeFirst();
				//outputExecution("Pop Top", thread);
			} finally {
				this.topReentratLock.unlock();
				unblockOtherEnd(bottomReentratLock);
			}
		}
		return null;
	}
	
	public boolean isEmpty() {
		//return deque.size() == 0;
		return false;
	}
	
	private void outputExecution(String operation, LockeThread thread) {
		System.out.println("Thread ID: " + thread + " is doing " + operation);
		//System.out.println("Thread ID: " + thread + " is doing " + "deque Current Size: " + deque.size());
	}

	private void blockOtherEnd(Lock lock) {
		lock.lock();
	}

	private void unblockOtherEnd(Lock lock) {
		lock.unlock();
	}

}

