package main.java.edu.utexas.locke;

public class ForkOperation extends SynchronizationOperation {

	private LockeThread childThread;
	
	public ForkOperation(LockeThread childThread) {
		this.childThread = childThread;
	}
	
	public LockeThread getChildThread() {
		return childThread;		
	}
	
	@Override
	public Type getType() {
		return SynchronizationOperation.Type.FORK;
	}

}
