 package main.java.edu.utexas.locke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class LockeThreadPool {

	// @TODO: When the initial set of LockeThreads are added to
	// the LockeThreadPool, we need to call
	// ComputationTracker.initialize(numLockeThreads);
	// This will allow us to keep track of when all LockeThreads
	// have completed.

	private static List<LockeProcess> processes;
	private Semaphore startSemaphore;

	public LockeThreadPool(int numProcesses) {
		startSemaphore = new Semaphore(0);
		final CountDownLatch cdl = new CountDownLatch(numProcesses);

		processes = new ArrayList<LockeProcess>();

		for (int i = 0; i < numProcesses; i++) {
			LockeProcess process = new LockeProcess(startSemaphore);
			process.start();
			processes.add(process);
		}

		Thread dequeMonitor = new Thread(
			new Runnable() {
				@Override
				public void run() {
					while (!ComputationTracker.done()) {
						String sizes = "";
						for (int i = 0; i < processes.size(); i++) {
							sizes += processes.get(i).getDeque().size() + "\t";
						}
						System.out.println(sizes);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
		);
		dequeMonitor.start();
	}

	public void submit(Collection<LockeThread> threads) {
		for (LockeThread thread : threads) {
			getRandomProcess().submit(thread);
		}

		startSemaphore.release(processes.size());
	}

	public <T> T invoke(LockeTask<T> task) {
		ComputationTracker.increment();
		startSemaphore.release(processes.size());
		return getRandomProcess().invokeTask(task);
	}

	static LockeProcess getRandomProcess() {
		Random random = new Random();
		return processes.get(random.nextInt(processes.size()));
	}
}
