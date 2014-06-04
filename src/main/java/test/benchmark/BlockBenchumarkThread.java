package test.benchmark;

import java.util.concurrent.TimeUnit;

final class BlockBenchumarkThread implements Runnable {

    @Override
    public void run() {
        controller.waitForBarrier();
        int index = 0;
        resetToken();
        while (true) {
            if (index > controller.addCount) {
                break;
            }

            if (token > 0) {
                controller.incrementCounter();
                token--;
                index++;
            } else {
                try {
                    applyToken();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        controller.waitForBarrier();
    }

    /**
     * Apply token.
     * 
     * @throws InterruptedException
     */
    private void applyToken() throws InterruptedException {
        while (true) {
            if (controller.getCounterValue() >= expect) {
                resetToken();
                break;
            } else {
                TimeUnit.NANOSECONDS.sleep(2L);
            }
        }
    }

    private void resetToken() {
        token = controller.tokenSize;
        expect += token * controller.numThreads;
    }

    BlockBenchumarkThread(final AbstractBenchmark controller) {
        this.controller = controller;
    }

    /** The controller. */
    private AbstractBenchmark controller;

    /** The token size. */
    private int token;

    /** The expect. */
    private long expect;

}
