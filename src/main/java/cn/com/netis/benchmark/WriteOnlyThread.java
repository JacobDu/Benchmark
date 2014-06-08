package cn.com.netis.benchmark;

final class WriteOnlyThread implements Runnable {

    public void run() {
        controller.waitForBarrier();
        for (int i = 0; i < controller.addCount; i++) {
            controller.incrementCounter();
        }
        controller.waitForBarrier();
    }

    WriteOnlyThread(AbstractBenchmark controller) {
        this.controller = controller;
    }

    /** The controller. */
    private AbstractBenchmark controller;
}
