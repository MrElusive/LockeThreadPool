package main.java.edu.utexas.locke;

public abstract class SynchronizationOperation {
	enum Type {
		BLOCK,
		FORK,
		JOIN
	}
	
	private Type type = null;
	
	public abstract Type getType();
	
}
