package test.benchmark;

final class IncrementingBenchmarkThread implements Runnable {
	protected AbstractBenchmark controller;

	IncrementingBenchmarkThread(AbstractBenchmark controller) {
		this.controller = controller;
	}

	public void run() {
		controller.waitForBarrier();
		for (int i = 0; i < controller.addCount; i++) {
			controller.incrementCounter();
		}
		controller.waitForBarrier();
	}
}
