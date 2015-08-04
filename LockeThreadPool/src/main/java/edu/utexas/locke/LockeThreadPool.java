 package main.java.edu.utexas.locke;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

// Sierra
public class LockeThreadPool {

	// @TODO: When the initial set of LockeThreads are added to
	// the LockeThreadPool, we need to call 
	// ComputationTracker.initialize(numLockeThreads);
	// This will allow us to keep track of when all LockeThreads
	// have completed.

	Set<LockeProcess> processes;
	private ThreadPoolExecutor threadPoolExecutor;
	private Semaphore startSemaphore;
	
	@SuppressWarnings("unused")
	private ExecutorService threadPool;

	public LockeThreadPool(int numProcesses) {
		this.startSemaphore = new Semaphore(numProcesses, true);
		threadPool = Executors.newFixedThreadPool(numProcesses);
		final CountDownLatch cdl = new CountDownLatch(numProcesses);
		
		//processes = new HashSet<LockeProcess>();
		for (int i = 0; i < numProcesses; i++) {
			LockeProcess process = new LockeProcess(startSemaphore);
			process.start();
			processes.add(process);
		}
	}
	
	

	public void submit(Collection<LockeThread> threads) {
		for (LockeThread thread : threads) {
			getRandomProcess().submit(thread);
		}

		startSemaphore.release(processes.size());
	}

	
	
	
	public <T> T invoke(LockeTask<T> task) {
		
		
		try {
			startSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CountDownLatch count;
		
		return getRandomProcess().invokeTask(task);
		
	}
	
	


	public static LockeProcess getRandomProcess() {
		return null;
	}
	
	
	
	
	
	
}