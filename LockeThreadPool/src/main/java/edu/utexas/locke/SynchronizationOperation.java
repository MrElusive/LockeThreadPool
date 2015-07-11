package main.java.edu.utexas.locke;

public class SynchronizationOperation {
	enum Type {
		TERMINATE,
		BLOCK,
		FORK,
		JOIN
	};
	
	Type type;
	LockeThread readyThread;
	
	public SynchronizationOperation(Type type, LockeThread readyThread) {
		this.type = type;
		this.readyThread = readyThread;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public LockeThread getReadyThread() {
		return readyThread;
	}
	public void setReadyThread(LockeThread readyThread) {
		this.readyThread = readyThread;
	}
}
