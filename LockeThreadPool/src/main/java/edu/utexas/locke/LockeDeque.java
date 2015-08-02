package main.java.edu.utexas.locke;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

// Sierra
public class LockeDeque {
	
	private int size;
	private volatile Bottom bottom;
	private AtomicReference<Top> top;

	
	public LockeDeque() {
		bottom = new Bottom(new Node(size), size - 1);
		top = new AtomicReference<Top>(new Top(0, bottom.node, 0));
	}
	
	
	public LockeThread popBottom() {
		final Bottom oldBot = bottom; 
		final Bottom newBot = oldBot.index != size - 1 
				? new Bottom(oldBot.node, oldBot.index + 1)
				: new Bottom(oldBot.node.next, 0);
				
		bottom = newBot;
		final Top localTop = top.get(); 

		// When empty
		if (oldBot.node == localTop.node && oldBot.index == localTop.index) {
			bottom = new Bottom(oldBot.node, oldBot.index);
			return null;
		}

		final Object threadIdx = newBot.node.array.get(newBot.index); 
		// Process the last thread 
		if (newBot.node == localTop.node && newBot.index == localTop.index) {
			final Top newTopThread = new Top(localTop.tag + 1, localTop.node, localTop.index);
			// CAS Success
			if (top.compareAndSet(localTop, newTopThread)) {  
				return (LockeThread) threadIdx;
			}
			// CAS fail, return the old bottom
			bottom = oldBot;
			return null;
		}
		return (LockeThread) threadIdx;
	}

	// Process from thread bottom
	public void pushBottom(final LockeThread thread) {
		final Bottom local = bottom;
		local.node.array.set(local.index, thread); 
		if (local.index != 0) {
			bottom = new Bottom(local.node, local.index - 1); 
		} else { 
			final Node newNode = new Node(size);
			newNode.next = local.node;
			local.node.pre = newNode;
			bottom = new Bottom(newNode, size - 1);
		}
	}

	//Steal from the top
	public LockeThread popTop() {
		//Get the Top
		final Top localTop = top.get(); 
		// Get the bottom
		final Bottom localBottom = bottom; 

		if (Empty(localBottom, localTop)) {
	   		if (localTop == top.get()) {
	   			return null; 
	   		}
			return null; 
		}

		final Top newTopThread = localTop.index != 0  // keep in current position
				? new Top(localTop.tag, localTop.node, localTop.index - 1) //go to next node and tag+1
				: new Top(localTop.tag + 1, localTop.node.pre, size - 1);

		final Object threadIdx = localTop.node.array.get(localTop.index);
		if (!top.compareAndSet(localTop, newTopThread)) {
			return null; 
		}
		return (LockeThread) threadIdx;
	}
	
	private static class Node {
		final AtomicReferenceArray<Object> array;
		volatile Node pre, next;

		Node(final int arraySize) {
			array = new AtomicReferenceArray<Object>(arraySize);
		}
	}
	
	private static class Bottom {
		final Node node;
		final int index;

		Bottom (final Node node, final int index) {
			this.node = node;
			this.index = index;
		}
	}

	private static class Top {
		final int tag;
		final Node node;
		final int index;

		Top (final int tag, final Node node, final int index) {
			this.tag = tag;
			this.node = node;
			this.index = index;
		}
	}
	
	private boolean Empty(final Bottom bot, final Top top) {
		if (bot.node == top.node && (bot.index == top.index || bot.index == top.index + 1)) {
			return true;
		}
		if (bot.node == top.node.next && bot.index == 0 && top.index == size - 1) {
			return true;
		}
		return false;
	}

}
