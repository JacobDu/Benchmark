package test.benchmark;

final class IncrementingBenchmarkThread implements Runnable {

    public void run() {
        controller.waitForBarrier();
        for (int i = 0; i < controller.addCount; i++) {
            controller.incrementCounter();
        }
        controller.waitForBarrier();
    }

    IncrementingBenchmarkThread(AbstractBenchmark controller) {
        this.controller = controller;
    }

    /** The controller. */
    private AbstractBenchmark controller;
}
