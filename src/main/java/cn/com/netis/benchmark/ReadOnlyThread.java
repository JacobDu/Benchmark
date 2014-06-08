package cn.com.netis.benchmark;

class ReadOnlyThread implements Runnable {

    @Override
    public void run() {
        controller.waitForBarrier();
        for (int i = 0; i < controller.addCount; i++) {
            controller.getCounterValue();
        }
        controller.waitForBarrier();
    }

    ReadOnlyThread(AbstractBenchmark controller) {
        this.controller = controller;
    }

    /** The controller. */
    private AbstractBenchmark controller;

}
