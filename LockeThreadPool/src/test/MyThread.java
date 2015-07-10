package test;

import edu.utexas.locke.ExecutionContext;
import edu.utexas.locke.LockeThread;

public class MyThread implements LockeThread {
	private String name;

	public MyThread(String name) {
		this.name = name;		
	}

	@Override
	public void run(ExecutionContext context) {
		System.out.println("Hi! My name is " + name);		
	}

}
