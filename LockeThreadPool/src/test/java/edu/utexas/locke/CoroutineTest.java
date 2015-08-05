package test.java.edu.utexas.locke;

import com.offbynull.coroutines.user.Continuation;
import com.offbynull.coroutines.user.Coroutine;
import com.offbynull.coroutines.user.CoroutineRunner;

public class CoroutineTest {

	private static final class MyCoroutine implements Coroutine {

		@Override
		public void run(Continuation c) {
			System.out.println("started!");
			for (int i = 0; i < 10; i++)
			{
				echo(c, i);
			}
		}

		private void echo(Continuation c, int x) {
			System.out.println("in echo!");
			System.out.println(x);
			c.suspend();
		}
	}

	public static void main(String[] args) {
		CoroutineRunner runner = new CoroutineRunner(new MyCoroutine());
		while (runner.execute()) {
			System.out.println("returned from coroutine");
			System.out.println("back in main!");
		}
		System.out.println("finished!");
	}

}
