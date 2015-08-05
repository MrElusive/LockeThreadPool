package test.java.edu.utexas.locke;

import main.java.edu.utexas.locke.LockeTask;
import main.java.edu.utexas.locke.LockeThreadPool;

import com.offbynull.coroutines.user.Continuation;

class LockeFibonacci extends LockeTask<Integer> {
	final int n;

	LockeFibonacci(int n) {
		this.n = n;
	}

	@Override
	protected Integer compute(Continuation c) {
		if ((n == 0) || (n == 1)) {
			return 1;
		}
		LockeFibonacci f1 = new LockeFibonacci(n - 1);
		this.fork(c, f1);
		LockeFibonacci f2 = new LockeFibonacci(n - 2);
		return f2.compute(c) + f1.waitGet(c);
	}

	public static void main(String[] args) {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processors);

		int n = 5;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}
		LockeFibonacci f = new LockeFibonacci(n);

		LockeThreadPool pool = new LockeThreadPool(processors);
		int result = pool.invoke(f);
		System.out.println("Result: " + result);
	}
}
