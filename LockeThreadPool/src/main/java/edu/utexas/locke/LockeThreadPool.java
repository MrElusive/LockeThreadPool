package main.java.edu.utexas.locke;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

/*
 * The LockeProcess container. Tasks are synchronously executed by the LockeThreadPool
 * via the invoke() method. LockeProcesses receive an instance of the LockeThreadPool to which
 * they are added, so that they may be able to choose a random() process to steal from.
 */
public class LockeThreadPool {
	private static final boolean runDequeMonitor = false;
	private List<LockeProcess> processes;
	private Semaphore startSemaphore;

	public LockeThreadPool(int numProcesses) {
		startSemaphore = new Semaphore(0);

		processes = new ArrayList<LockeProcess>();

		for (int i = 0; i < numProcesses; i++) {
			LockeProcess process = new LockeProcess(this, startSemaphore);
			process.start();
			processes.add(process);
		}
	}

	// Synchronously invokes a single task.
	// @TODO: Unfortunately, With the current implementation, the LockeProcesses in this pool
	// are spent after the first invoke(). We need to fix this and also implement a submit() method
	// for asynchronous execution.
	public <T> T invoke(LockeTask<T> task) {
		ComputationTracker.increment();

		if (runDequeMonitor) {
			startDequeMonitor();
		}

		getRandomProcess().getDeque().pushBottom(task);
		startSemaphore.release(processes.size());
		try {
			task.waitDone();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return task.get();
	}

	// Starts an extra thread that monitors and logs the sizes of the deques
	// of every process in the pool
	private void startDequeMonitor() {
		Thread dequeMonitor = new Thread(new Runnable() {
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
		});
		dequeMonitor.start();
	}

	LockeProcess getRandomProcess() {
		Random random = new Random();
		return processes.get(random.nextInt(processes.size()));
	}
}
